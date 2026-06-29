package com.example.attendease.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseResponse(
    val id: String,
    @SerialName("course_code") val courseCode: String,
    val title: String,
    @SerialName("credit_units") val creditUnits: Int,
    @SerialName("department_id") val departmentId: String,
    val department: DepartmentResponse? = null,
    @SerialName("course_assignments") val courseAssignments: List<CourseAssignmentResponse>? = null
)

@Serializable
data class AcademicSessionResponse(
    val id: String,
    @SerialName("session_name") val sessionName: String,
    val semester: String,
    @SerialName("is_active") val isActive: Boolean = false,
    @SerialName("start_date") val startDate: String = "",
    @SerialName("end_date") val endDate: String = ""
)


@Serializable
data class CourseAssignmentResponse(
    val id: String,
    @SerialName("course_id") val courseId: String?,
    @SerialName("lecturer_id") val lecturerId: String?,
    @SerialName("academic_session_id") val academicSessionId: String?,
    val course: CourseResponse? = null,
    val lecturer: com.example.attendease.dto.response.LecturerResponse? = null,
    @SerialName("academic_session") val academicSession: AcademicSessionResponse? = null
)
