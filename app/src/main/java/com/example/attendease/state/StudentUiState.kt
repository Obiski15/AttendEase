package com.example.attendease.state

import com.example.attendease.dto.response.StudentResponse
import com.example.attendease.dto.response.DepartmentResponse

data class StudentUiState(
    val students: List<StudentResponse> = emptyList(),
    val departments: List<DepartmentResponse> = emptyList(),
    val currentStudent: StudentResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)
