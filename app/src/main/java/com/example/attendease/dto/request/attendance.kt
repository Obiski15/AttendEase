package com.example.attendease.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceSessionCreateRequest(
    @SerialName("course_assignment_id") val courseAssignmentId: String,
    @SerialName("session_date") val sessionDate: String? = null,
    @SerialName("duration_minutes") val durationMinutes: Int? = null,
    @SerialName("session_code") val sessionCode: String? = null,
    @SerialName("geofencing_enabled") val geofencingEnabled: Boolean? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerialName("radius_meters") val radiusMeters: Int? = null
)

@Serializable
data class AttendanceCheckInRequest(
    @SerialName("session_code") val sessionCode: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)
