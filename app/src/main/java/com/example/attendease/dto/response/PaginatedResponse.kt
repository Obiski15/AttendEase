package com.example.attendease.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
