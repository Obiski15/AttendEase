package com.example.attendease.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.attendease.data.local.entity.ApiCacheEntity

@Dao
interface ApiCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApiCache(cache: ApiCacheEntity): Long

    @Query("SELECT * FROM api_cache WHERE cacheKey = :key")
    fun getApiCache(key: String): ApiCacheEntity?
}
