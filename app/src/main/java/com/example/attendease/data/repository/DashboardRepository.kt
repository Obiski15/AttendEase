package com.example.attendease.data.repository

import android.util.Log
import com.example.attendease.data.api.DashboardApi
import com.example.attendease.data.local.dao.DashboardDao
import com.example.attendease.data.local.dao.SyncDao
import com.example.attendease.data.local.entity.DashboardCacheEntity
import com.example.attendease.dto.request.AttendanceSessionCreateRequest
import com.example.attendease.dto.response.AdminDashboardResponse
import com.example.attendease.dto.response.LecturerActiveSessionResponse
import com.example.attendease.dto.response.LecturerDashboardResponse
import com.example.attendease.dto.response.StudentDashboardResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DashboardRepository(
    private val dashboardApi: DashboardApi,
    private val dashboardDao: DashboardDao,
    private val syncDao: SyncDao
) {
    suspend fun getAdminDashboard(): AdminDashboardResponse {
        return try {
            val response = dashboardApi.getAdminDashboard()
            withContext(Dispatchers.IO) {
                dashboardDao.insertDashboardCache(
                    DashboardCacheEntity(
                        role = "ADMIN",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("DashboardRepo", "Network failed, loading ADMIN cache", e)
            val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("ADMIN") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
    }

    private suspend fun mergePendingSessions(response: LecturerDashboardResponse): LecturerDashboardResponse {
        val pendingActions = withContext(Dispatchers.IO) { syncDao.getPendingActions() }
        
        val pendingCloseActions = pendingActions.filter { it.actionType == "CLOSE_SESSION" }
        val locallyClosedSessionIds = pendingCloseActions.mapNotNull { action ->
            try {
                val map = Json.decodeFromString<Map<String, String>>(action.payloadJson)
                map["session_id"]
            } catch (e: Exception) {
                null
            }
        }.toSet()

        val pendingStartActions = pendingActions.filter { it.actionType == "START_SESSION" }
        val pendingSessions = pendingStartActions.mapNotNull { action ->
            val req = Json.decodeFromString<AttendanceSessionCreateRequest>(action.payloadJson)
            val sessionId = req.id ?: ""
            
            if (sessionId in locallyClosedSessionIds) return@mapNotNull null

            val course = response.courses.find { it.courseAssignmentId == req.courseAssignmentId }
            val localExpiresAt = java.time.Instant.ofEpochMilli(action.createdAt)
                .plusSeconds((req.durationMinutes ?: 60) * 60L).toString()
                
            LecturerActiveSessionResponse(
                id = sessionId,
                courseCode = course?.courseCode ?: "Unknown",
                courseTitle = course?.courseTitle ?: "Unknown",
                sessionCode = req.sessionCode ?: "PENDING",
                expiresAt = localExpiresAt,
                geofencingEnabled = req.geofencingEnabled ?: false,
                radiusMeters = req.radiusMeters
            )
        }

        val filteredActiveSessions = response.activeSessions.filter { it.id !in locallyClosedSessionIds }

        return response.copy(
            activeSessions = filteredActiveSessions + pendingSessions
        )
    }

    suspend fun getLecturerDashboard(): LecturerDashboardResponse {
        return try {
            val response = dashboardApi.getLecturerDashboard()
            withContext(Dispatchers.IO) {
                dashboardDao.insertDashboardCache(
                    DashboardCacheEntity(
                        role = "LECTURER",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            mergePendingSessions(response)
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("DashboardRepo", "Network failed, loading LECTURER cache", e)
            val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("LECTURER") }
            if (cache != null) {
                val cachedResponse = Json.decodeFromString<LecturerDashboardResponse>(cache.payloadJson)
                mergePendingSessions(cachedResponse)
            } else {
                throw e
            }
        }
    }

    suspend fun getStudentDashboard(): StudentDashboardResponse {
        return try {
            val response = dashboardApi.getStudentDashboard()
            withContext(Dispatchers.IO) {
                dashboardDao.insertDashboardCache(
                    DashboardCacheEntity(
                        role = "STUDENT",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("DashboardRepo", "Network failed, loading STUDENT cache", e)
            val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("STUDENT") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
    }

    suspend fun getCachedAdminDashboard(): AdminDashboardResponse? {
        val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("ADMIN") }
        return if (cache != null) Json.decodeFromString(cache.payloadJson) else null
    }

    suspend fun getCachedLecturerDashboard(): LecturerDashboardResponse? {
        val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("LECTURER") }
        return if (cache != null) {
            val response = Json.decodeFromString<LecturerDashboardResponse>(cache.payloadJson)
            mergePendingSessions(response)
        } else null
    }

    suspend fun getCachedStudentDashboard(): StudentDashboardResponse? {
        val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("STUDENT") }
        return if (cache != null) Json.decodeFromString(cache.payloadJson) else null
    }
}
