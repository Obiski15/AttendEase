package com.example.attendease.worker

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.attendease.R
import com.example.attendease.data.api.AttendanceSessionApi
import com.example.attendease.data.local.dao.SyncDao
import com.example.attendease.dto.request.AttendanceSessionCreateRequest
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val syncDao: SyncDao by inject()
    private val attendanceSessionApi: AttendanceSessionApi by inject()

    override suspend fun doWork(): Result {
        return try {
            val pendingActions = syncDao.getPendingActions()
            if (pendingActions.isEmpty()) {
                return Result.success()
            }

            var successCount = 0
            for (action in pendingActions) {
                try {
                    when (action.actionType) {
                        "START_SESSION" -> {
                            val request = Json.decodeFromString<AttendanceSessionCreateRequest>(action.payloadJson)
                            attendanceSessionApi.openSession(request)
                        }
                        "CHECK_IN" -> {
                            val request = Json.decodeFromString<com.example.attendease.dto.request.AttendanceCheckInRequest>(action.payloadJson)
                            val attendanceApi: com.example.attendease.data.api.AttendanceApi by inject()
                            attendanceApi.checkIn(request)
                        }
                        "CLOSE_SESSION" -> {
                            val map = Json.decodeFromString<Map<String, String>>(action.payloadJson)
                            val sessionId = map["session_id"] ?: throw IllegalArgumentException("Missing session_id in payload")
                            attendanceSessionApi.closeSession(sessionId)
                        }
                        // Add more actions like "CHECK_IN" here
                    }
                    
                    // Mark as synced
                    syncDao.updateSyncAction(action.copy(status = "SYNCED"))
                    successCount++
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Failed to sync action: ${action.id}", e)
                    // Continue with other actions, we'll retry this one later
                }
            }
            
            // Optionally clean up synced actions
            syncDao.deleteSyncedActions()
            
            if (successCount > 0) {
                showSyncNotification(successCount)
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed", e)
            return Result.retry()
        }
    }

    private fun showSyncNotification(successCount: Int) {
        val context = applicationContext
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(context, "offline_sync_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Offline Sync Complete")
                .setContentText("Successfully synced $successCount offline action(s).")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            NotificationManagerCompat.from(context).notify(1001, builder.build())
        }
    }
}
