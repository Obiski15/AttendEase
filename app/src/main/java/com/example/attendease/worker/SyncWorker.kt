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
import com.example.attendease.data.api.UnauthorizedException
import com.example.attendease.data.local.dao.SyncDao
import com.example.attendease.dto.request.AttendanceSessionCreateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val syncDao: SyncDao by inject()
    private val attendanceSessionApi: AttendanceSessionApi by inject()
    private val attendanceApi: com.example.attendease.data.api.AttendanceApi by inject()

    override suspend fun doWork(): Result {
        Log.i("SyncWorker", "SyncWorker started. Run attempt: $runAttemptCount")
        return try {
            val pendingActions = withContext(Dispatchers.IO) { syncDao.getPendingActions() }
            Log.i("SyncWorker", "Found ${pendingActions.size} pending actions")
            if (pendingActions.isEmpty()) {
                return Result.success()
            }

            var successCount = 0
            for (action in pendingActions) {
                Log.i("SyncWorker", "Processing action id=${action.id} type=${action.actionType} status=${action.status}")
                try {
                    when (action.actionType) {
                        "START_SESSION" -> {
                            val request = Json.decodeFromString<AttendanceSessionCreateRequest>(action.payloadJson)
                            Log.i("SyncWorker", "Syncing START_SESSION: id=${request.id}, assignmentId=${request.courseAssignmentId}")
                            attendanceSessionApi.openSession(request)
                            Log.i("SyncWorker", "START_SESSION synced successfully")
                        }
                        "CHECK_IN" -> {
                            val request = Json.decodeFromString<com.example.attendease.dto.request.AttendanceCheckInRequest>(action.payloadJson)
                            Log.i("SyncWorker", "Syncing CHECK_IN: code=${request.sessionCode}")
                            attendanceApi.checkIn(request)
                            Log.i("SyncWorker", "CHECK_IN synced successfully")
                        }
                        "CLOSE_SESSION" -> {
                            val map = Json.decodeFromString<Map<String, String>>(action.payloadJson)
                            val sessionId = map["session_id"] ?: throw IllegalArgumentException("Missing session_id in payload")
                            Log.i("SyncWorker", "Syncing CLOSE_SESSION: sessionId=$sessionId")
                            attendanceSessionApi.closeSession(sessionId)
                            Log.i("SyncWorker", "CLOSE_SESSION synced successfully")
                        }
                    }

                    withContext(Dispatchers.IO) {
                        syncDao.updateSyncAction(action.copy(status = "SYNCED"))
                    }
                    successCount++
                } catch (e: UnauthorizedException) {
                    Log.e("SyncWorker", "Auth expired for action ${action.id}. Marking FAILED_PERMANENTLY.", e)
                    withContext(Dispatchers.IO) {
                        syncDao.updateSyncAction(action.copy(status = "FAILED_PERMANENTLY"))
                    }
                } catch (e: com.example.attendease.data.api.ApiException) {
                    if (e.code == 400) {
                        Log.e("SyncWorker", "Action ${action.id} rejected by server (400): ${e.message}. Dropping.", e)
                        withContext(Dispatchers.IO) {
                            syncDao.updateSyncAction(action.copy(status = "FAILED_PERMANENTLY"))
                        }
                    } else if (e.code == 409) {
                        Log.w("SyncWorker", "Action ${action.id} already exists on server (409). Marking SYNCED.", e)
                        withContext(Dispatchers.IO) {
                            syncDao.updateSyncAction(action.copy(status = "SYNCED"))
                        }
                        successCount++
                    } else if (e.code == 404) {
                        val ageHours = (System.currentTimeMillis() - action.createdAt) / (1000 * 60 * 60)
                        if (ageHours > 168) {
                            Log.e("SyncWorker", "Action ${action.id} is 404 and older than 7 days. Dropping.", e)
                            withContext(Dispatchers.IO) {
                                syncDao.updateSyncAction(action.copy(status = "FAILED_PERMANENTLY"))
                            }
                        } else {
                            Log.w("SyncWorker", "Action ${action.id} is 404. Will retry later. Age: $ageHours hours.", e)
                            throw e
                        }
                    } else {
                        Log.e("SyncWorker", "API error for action ${action.id}: ${e.code} - ${e.message}. Will retry.", e)
                        throw e
                    }
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Network/Unknown error for action ${action.id}: ${e::class.simpleName} - ${e.message}", e)
                    throw e
                }
            }

            withContext(Dispatchers.IO) { syncDao.deleteSyncedActions() }

            if (successCount > 0) {
                Log.i("SyncWorker", "Sync complete. $successCount actions synced.")
                showSyncNotification(successCount)
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed, will retry. Error: ${e::class.simpleName} - ${e.message}", e)
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
