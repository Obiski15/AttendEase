package com.example.attendease.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentCreateRequest(
    val email: String,
    val password: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("student_id") val studentId: String,
    @SerialName("matric_number") val matricNumber: String,
    @SerialName("department_id") val departmentId: String,
    val level: String
)

@Serializable
data class StudentUpdateRequest(
    @SerialName("student_id") val studentId: String? = null,
    @SerialName("matric_number") val matricNumber: String? = null,
    @SerialName("department_id") val departmentId: String? = null,
    val level: String? = null
)
