package com.example.attendease.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LecturerResponse(
    @SerialName("staff_id") val staffId: String?,
    @SerialName("department_id") val departmentId: String?,
    @SerialName("user_id") val userId: String,
    val user: UserResponse? = null
)
