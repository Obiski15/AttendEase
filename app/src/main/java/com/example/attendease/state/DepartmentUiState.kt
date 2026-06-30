package com.example.attendease.state

import com.example.attendease.dto.response.DepartmentResponse

data class DepartmentUiState(
    val departments: List<DepartmentResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)
