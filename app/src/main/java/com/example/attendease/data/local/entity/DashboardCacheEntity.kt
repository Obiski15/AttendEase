package com.example.attendease.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dashboard_cache")
data class DashboardCacheEntity(
    @PrimaryKey val role: String, // "ADMIN", "LECTURER", "STUDENT"
    val payloadJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)
