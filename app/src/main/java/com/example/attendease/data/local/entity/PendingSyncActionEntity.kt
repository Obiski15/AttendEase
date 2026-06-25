package com.example.attendease.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_sync_actions")
data class PendingSyncActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val actionType: String,
    val payloadJson: String,
    val status: String = "PENDING",
    val createdAt: Long = System.currentTimeMillis()
)
