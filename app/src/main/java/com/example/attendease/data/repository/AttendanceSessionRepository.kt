package com.example.attendease.data.repository

import com.example.attendease.data.api.AttendanceSessionApi
import com.example.attendease.dto.request.AttendanceSessionCreateRequest
import com.example.attendease.dto.response.AttendanceRecordResponse
import com.example.attendease.dto.response.AttendanceSessionResponse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.dao.SyncDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import com.example.attendease.data.local.entity.PendingSyncActionEntity
import com.example.attendease.worker.SyncWorker
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class AttendanceSessionRepository(
    private val attendanceSessionApi: AttendanceSessionApi,
    private val syncDao: SyncDao,
    private val apiCacheDao: ApiCacheDao,
    private val workManager: WorkManager
) {
    suspend fun openSession(request: AttendanceSessionCreateRequest): AttendanceSessionResponse {
        return try {
            attendanceSessionApi.openSession(request)
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.e("AttendanceSessionRepo", "Network failed, queueing offline action", e)
            
            // Generate UUID and Session Code locally
            val localId = UUID.randomUUID().toString()
            val localCode = run {
                val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
                val secureRandom = java.security.SecureRandom()
                (1..6).map { alphabet[secureRandom.nextInt(alphabet.length)] }.joinToString("")
            }

            // Create a new request object with the injected ID
            val requestWithId = request.copy(id = localId, sessionCode = localCode)

            // Queue for offline sync
            val payload = Json.encodeToString(requestWithId)
            withContext(Dispatchers.IO) {
                syncDao.insertSyncAction(
                    PendingSyncActionEntity(
                        actionType = "START_SESSION",
                        payloadJson = payload
                    )
                )
            }
            
            // Enqueue work
            val constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                .build()
            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()
            workManager.enqueue(syncRequest)

            // Return a local pending response so the UI proceeds immediately with the valid ID and Code
            val localExpiresAt = java.time.Instant.now().plusSeconds((request.durationMinutes ?: 60) * 60L).toString()
            AttendanceSessionResponse(
                id = localId,
                courseAssignmentId = request.courseAssignmentId,
                sessionDate = null,
                startTime = null,
                expiresAt = localExpiresAt,
                sessionCode = localCode,
                status = "PENDING",
                geofencingEnabled = request.geofencingEnabled,
                latitude = request.latitude,
                longitude = request.longitude,
                radiusMeters = request.radiusMeters
            )
        }
    }

    suspend fun closeSession(sessionId: String): AttendanceSessionResponse {
        return try {
            attendanceSessionApi.closeSession(sessionId)
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.e("AttendanceSessionRepo", "Network failed, queueing closeSession", e)
            val payload = Json.encodeToString(mapOf("session_id" to sessionId))
            withContext(Dispatchers.IO) {
                syncDao.insertSyncAction(PendingSyncActionEntity(actionType = "CLOSE_SESSION", payloadJson = payload))
            }
            val constraints = androidx.work.Constraints.Builder().setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build()
            workManager.enqueue(OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(constraints).build())

            AttendanceSessionResponse(
                id = sessionId,
                courseAssignmentId = null,
                sessionDate = null,
                startTime = null,
                expiresAt = null,
                sessionCode = "CLOSED",
                status = "CLOSED",
                geofencingEnabled = null,
                latitude = null,
                longitude = null,
                radiusMeters = null
            )
        }
    }

    suspend fun getSessionRecords(sessionId: String): List<AttendanceRecordResponse> {
        return try {
            val response = attendanceSessionApi.getSessionRecords(sessionId)
            withContext(Dispatchers.IO) {
                apiCacheDao.insertApiCache(ApiCacheEntity(cacheKey = "session_records_$sessionId", payloadJson = Json.encodeToString(response)))
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("AttendanceSessionRepo", "Network failed, loading session records cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("session_records_$sessionId") }
            if (cache != null) Json.decodeFromString(cache.payloadJson) else throw e
        }
    }

    suspend fun getAttendanceSessions(): List<AttendanceSessionResponse> {
        return try {
            val response = attendanceSessionApi.getAttendanceSessions()
            withContext(Dispatchers.IO) {
                apiCacheDao.insertApiCache(ApiCacheEntity(cacheKey = "lecturer_sessions", payloadJson = Json.encodeToString(response)))
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("AttendanceSessionRepo", "Network failed, loading sessions cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("lecturer_sessions") }
            val cachedSessions = if (cache != null) Json.decodeFromString<List<AttendanceSessionResponse>>(cache.payloadJson) else emptyList()
            
            val pendingActions = withContext(Dispatchers.IO) { syncDao.getPendingActions() }
            val pendingSessions = pendingActions.filter { it.actionType == "START_SESSION" }.map { action ->
                val req = Json.decodeFromString<AttendanceSessionCreateRequest>(action.payloadJson)
                AttendanceSessionResponse(
                    id = req.id ?: "",
                    courseAssignmentId = req.courseAssignmentId,
                    sessionDate = req.sessionDate,
                    startTime = null,
                    expiresAt = null,
                    sessionCode = req.sessionCode ?: "",
                    status = "PENDING",
                    geofencingEnabled = req.geofencingEnabled,
                    latitude = req.latitude,
                    longitude = req.longitude,
                    radiusMeters = req.radiusMeters
                )
            }
            
            if (cachedSessions.isEmpty() && pendingSessions.isEmpty()) throw e
            cachedSessions + pendingSessions
        }
    }
}
