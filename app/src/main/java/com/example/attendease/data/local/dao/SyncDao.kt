package com.example.attendease.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.attendease.data.local.entity.PendingSyncActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSyncAction(action: PendingSyncActionEntity): Long

    @Update
    fun updateSyncAction(action: PendingSyncActionEntity): Int

    @Query("SELECT * FROM pending_sync_actions WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingActions(): List<PendingSyncActionEntity>

    @Query("SELECT * FROM pending_sync_actions ORDER BY createdAt DESC")
    fun getAllActionsFlow(): Flow<List<PendingSyncActionEntity>>

    @Query("DELETE FROM pending_sync_actions WHERE status = 'SYNCED'")
    fun deleteSyncedActions(): Int
}
