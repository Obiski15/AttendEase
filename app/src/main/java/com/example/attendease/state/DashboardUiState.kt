package com.example.attendease.state

import com.example.attendease.dto.response.AdminDashboardResponse
import com.example.attendease.dto.response.LecturerDashboardResponse
import com.example.attendease.dto.response.StudentDashboardResponse

data class DashboardUiState(
    val adminStats: AdminDashboardResponse? = null,
    val lecturerStats: LecturerDashboardResponse? = null,
    val studentStats: StudentDashboardResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
