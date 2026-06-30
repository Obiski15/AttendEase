package com.example.attendease.state

import com.example.attendease.dto.response.CourseResponse

data class CourseUiState(
    val courses: List<CourseResponse> = emptyList(),
    val currentCourse: CourseResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)
