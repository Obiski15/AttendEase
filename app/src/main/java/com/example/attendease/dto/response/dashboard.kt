package com.example.attendease.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminDashboardResponse(
    @SerialName("total_students") val totalStudents: Int,
    @SerialName("total_lecturers") val totalLecturers: Int,
    @SerialName("total_courses") val totalCourses: Int,
    @SerialName("active_sessions") val activeSessions: Int
)
