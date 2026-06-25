package com.example.attendease.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.dao.DashboardDao
import com.example.attendease.data.local.dao.SyncDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import com.example.attendease.data.local.entity.DashboardCacheEntity
import com.example.attendease.data.local.entity.PendingSyncActionEntity

@Database(
    entities = [
        PendingSyncActionEntity::class,
        DashboardCacheEntity::class,
        ApiCacheEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun syncDao(): SyncDao
    abstract fun dashboardDao(): DashboardDao
    abstract fun apiCacheDao(): ApiCacheDao
}
