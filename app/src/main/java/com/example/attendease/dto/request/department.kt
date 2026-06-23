package com.example.attendease.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class DepartmentCreateRequest(
    val name: String
)
