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
                        // Add more actions here
                    }
                    
                    // Mark as synced
                    syncDao.updateSyncAction(action.copy(status = "SYNCED"))
                    successCount++
                } catch (e: com.example.attendease.data.api.ApiException) {
                    if (e.code == 400) {
                        Log.e("SyncWorker", "Action ${action.id} rejected by server (400). Dropping.", e)
                        syncDao.updateSyncAction(action.copy(status = "FAILED_PERMANENTLY"))
                    } 

                    // else if (e.code == 404) {
                    //    val ageHours = (System.currentTimeMillis() - action.createdAt) / (1000 * 60 * 60)
                    //    if (ageHours > 168) { // 7 days
                    //        Log.e("SyncWorker", "Action ${action.id} is 404 and older than 7 days. Dropping.", e)
                    //        syncDao.updateSyncAction(action.copy(status = "FAILED_PERMANENTLY"))
                    //    } else {
                    //        Log.w("SyncWorker", "Action ${action.id} is 404. Will retry later. Age: $ageHours hours.", e)
                    //        throw e // bubble up to retry
                    //    }
                    // }
                    
                    else {
                        Log.e("SyncWorker", "API error for action ${action.id}: ${e.code}. Will retry.", e)
                        throw e
                    }
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Network/Unknown error for action: ${action.id}", e)
                    throw e // Bubble up to cause WorkManager to retry
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
                .setContentTitle("Data Synchronization")
                .setContentText("$successCount pending ${if (successCount == 1) "record was" else "records were"} securely synced.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            NotificationManagerCompat.from(context).notify(1001, builder.build())
        }
    }
}
