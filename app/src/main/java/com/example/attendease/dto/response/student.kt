package com.example.attendease.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentResponse(
    @SerialName("student_id") val studentId: String?,
    @SerialName("matric_number") val matricNumber: String?,
    @SerialName("department_id") val departmentId: String?,
    val level: String?,
    @SerialName("user_id") val userId: String,
    val user: UserResponse? = null
)
