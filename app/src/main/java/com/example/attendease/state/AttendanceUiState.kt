package com.example.attendease.state

import com.example.attendease.dto.response.AttendanceRecordResponse
import com.example.attendease.dto.response.StudentDashboardResponse

data class AttendanceUiState(
    val attendanceHistory: List<AttendanceRecordResponse> = emptyList(),
    val studentStats: StudentDashboardResponse? = null,
    val checkInSuccess: AttendanceRecordResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
