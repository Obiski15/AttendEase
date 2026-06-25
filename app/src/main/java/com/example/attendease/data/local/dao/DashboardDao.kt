package com.example.attendease.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.attendease.data.local.entity.DashboardCacheEntity

@Dao
interface DashboardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDashboardCache(cache: DashboardCacheEntity): Long

    @Query("SELECT * FROM dashboard_cache WHERE role = :role")
    fun getDashboardCache(role: String): DashboardCacheEntity?
}
