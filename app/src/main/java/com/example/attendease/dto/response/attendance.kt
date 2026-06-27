package com.example.attendease.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceSessionResponse(
    val id: String,
    @SerialName("course_assignment_id") val courseAssignmentId: String?,
    @SerialName("session_date") val sessionDate: String?,
    @SerialName("start_time") val startTime: String?,
    @SerialName("expires_at") val expiresAt: String?,
    @SerialName("session_code") val sessionCode: String?,
    val status: String?,
    @SerialName("geofencing_enabled") val geofencingEnabled: Boolean?,
    val latitude: Double?,
    val longitude: Double?,
    @SerialName("radius_meters") val radiusMeters: Int?,
    @SerialName("records_count") val recordsCount: Int? = 0,
    @SerialName("total_students") val totalStudents: Int? = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class AttendanceRecordResponse(
    val id: String,
    @SerialName("session_id") val sessionId: String?,
    @SerialName("student_id") val studentId: String?,
    @SerialName("check_in_time") val checkInTime: String?,
    val latitude: Double?,
    val longitude: Double?,
    val status: String?,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("course_code") val courseCode: String? = null,
    @SerialName("course_title") val courseTitle: String? = null
)
