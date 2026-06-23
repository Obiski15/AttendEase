package com.example.attendease.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class DepartmentResponse(
    val id: String,
    val name: String
)
