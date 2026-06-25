package com.example.attendease.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_cache")
data class ApiCacheEntity(
    @PrimaryKey val cacheKey: String,
    val payloadJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)
