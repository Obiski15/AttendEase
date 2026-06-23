package com.example.attendease.dto.response

import com.example.attendease.enums.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    val user: UserResponse
)

@Serializable
data class UserResponse(
    val role: UserRole,
    @SerialName("full_name") val name: String? = null,
    val email: String? = null
)