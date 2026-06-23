package com.example.attendease.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LecturerCreateRequest(
    val email: String,
    val password: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("staff_id") val staffId: String,
    @SerialName("department_id") val departmentId: String
)

@Serializable
data class LecturerUpdateRequest(
    @SerialName("staff_id") val staffId: String? = null,
    @SerialName("department_id") val departmentId: String? = null
)
