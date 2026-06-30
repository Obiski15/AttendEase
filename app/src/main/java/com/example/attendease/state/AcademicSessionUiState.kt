package com.example.attendease.state

import com.example.attendease.dto.response.AcademicSessionResponse

data class AcademicSessionUiState(
    val sessions: List<AcademicSessionResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)
