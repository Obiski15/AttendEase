package com.example.attendease.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class TokenRefreshRequest(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class ChangePasswordRequest(
    @SerialName("old_password") val oldPassword: String,
    @SerialName("new_password") val newPassword: String
)

@Serializable
data class ProfileUpdateRequest(
    @SerialName("full_name") val name: String? = null,
    val email: String? = null
)