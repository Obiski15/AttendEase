package com.example.attendease.data.repository

import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.attendease.data.api.AttendanceApi
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.dao.SyncDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import com.example.attendease.data.local.entity.PendingSyncActionEntity
import com.example.attendease.dto.request.AttendanceCheckInRequest
import com.example.attendease.dto.response.AttendanceRecordResponse
import com.example.attendease.worker.SyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class AttendanceRepository(
    private val attendanceApi: AttendanceApi,
    private val syncDao: SyncDao,
    private val apiCacheDao: ApiCacheDao,
    private val workManager: WorkManager
) {
    suspend fun checkIn(request: AttendanceCheckInRequest): AttendanceRecordResponse {
        return try {
            attendanceApi.checkIn(request)
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.e("AttendanceRepo", "Network failed, queueing CHECK_IN", e)
            
            // Record the exact offline check-in time
            val offlineTime = java.time.Instant.now().toString()
            val requestWithTime = request.copy(checkInTime = offlineTime)

            val payload = Json.encodeToString(requestWithTime)
            withContext(Dispatchers.IO) {
                syncDao.insertSyncAction(
                    PendingSyncActionEntity(
                        actionType = "CHECK_IN",
                        payloadJson = payload
                    )
                )
            }
            
            val constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                .build()
            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()
            workManager.enqueue(syncRequest)

            AttendanceRecordResponse(
                id = UUID.randomUUID().toString(),
                sessionId = request.sessionCode,
                studentId = "offline",
                checkInTime = offlineTime,
                latitude = request.latitude,
                longitude = request.longitude,
                status = "PRESENT"
            )
        }
    }

    suspend fun getMyAttendance(): List<AttendanceRecordResponse> {
        return try {
            val response = attendanceApi.getMyAttendance()
            withContext(Dispatchers.IO) {
                apiCacheDao.insertApiCache(
                    ApiCacheEntity(
                        cacheKey = "my_attendance",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("AttendanceRepo", "Network failed, loading my_attendance cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("my_attendance") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
    }
}
