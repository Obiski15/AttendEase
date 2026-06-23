package com.example.attendease.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseResponse(
    val id: String,
    @SerialName("course_code") val courseCode: String,
    val title: String,
    @SerialName("credit_units") val creditUnits: Int,
    @SerialName("department_id") val departmentId: String
)

@Serializable
data class AcademicSessionResponse(
    val id: String,
    @SerialName("session_name") val sessionName: String,
    val semester: String,
    @SerialName("is_active") val isActive: Boolean = false
)

@Serializable
data class CourseAssignmentResponse(
    val id: String,
    @SerialName("course_id") val courseId: String?,
    @SerialName("lecturer_id") val lecturerId: String?,
    @SerialName("academic_session_id") val academicSessionId: String?
)
