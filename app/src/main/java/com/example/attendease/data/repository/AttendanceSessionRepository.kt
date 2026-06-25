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
            Log.e("AttendanceSessionRepo", "Network failed, queueing offline action", e)
            
            // Queue for offline sync
            val payload = Json.encodeToString(request)
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

            // Return a local pending response so the UI proceeds immediately
            AttendanceSessionResponse(
                id = UUID.randomUUID().toString(),
                courseAssignmentId = request.courseAssignmentId,
                sessionDate = null,
                startTime = null,
                expiresAt = null,
                sessionCode = "PENDING_SYNC",
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
            if (sessionId == "PENDING_SYNC") {
                throw IllegalStateException("Cannot close an offline session before it has been synced.")
            }
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
            Log.w("AttendanceSessionRepo", "Network failed, loading sessions cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("lecturer_sessions") }
            if (cache != null) Json.decodeFromString(cache.payloadJson) else throw e
        }
    }
}
