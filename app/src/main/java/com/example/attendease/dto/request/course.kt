package com.example.attendease.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseCreateRequest(
    @SerialName("course_code") val courseCode: String,
    val title: String,
    @SerialName("credit_units") val creditUnits: Int,
    @SerialName("department_id") val departmentId: String
)

@Serializable
data class CourseAssignmentCreateRequest(
    @SerialName("course_id") val courseId: String,
    @SerialName("lecturer_id") val lecturerId: String,
    @SerialName("academic_session_id") val academicSessionId: String
)

@Serializable
data class AcademicSessionCreateRequest(
    @SerialName("session_name") val sessionName: String,
    val semester: String,
    @SerialName("is_active") val isActive: Boolean = false,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String
)

@Serializable
data class CourseUpdateRequest(
    @SerialName("course_code") val courseCode: String? = null,
    val title: String? = null,
    @SerialName("credit_units") val creditUnits: Int? = null,
    @SerialName("department_id") val departmentId: String? = null
)

@Serializable
data class AcademicSessionUpdateRequest(
    @SerialName("session_name") val sessionName: String? = null,
    val semester: String? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null
)


