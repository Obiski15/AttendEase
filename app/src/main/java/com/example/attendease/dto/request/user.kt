package com.example.attendease.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRequest(
    val email: String,
    val password: String,
    @SerialName("full_name") val name: String,
    val role: String,
    val status: String = "ACTIVE"
)

@Serializable
data class UserUpdateRequest(
    val email: String? = null,
    @SerialName("full_name") val name: String? = null,
    val role: String? = null,
    val status: String? = null,
    val password: String? = null
)
