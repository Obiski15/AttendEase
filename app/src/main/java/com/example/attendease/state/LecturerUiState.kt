package com.example.attendease.state

import com.example.attendease.dto.response.LecturerResponse
import com.example.attendease.dto.response.DepartmentResponse

data class LecturerUiState(
    val lecturers: List<LecturerResponse> = emptyList(),
    val departments: List<DepartmentResponse> = emptyList(),
    val currentLecturer: LecturerResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)
