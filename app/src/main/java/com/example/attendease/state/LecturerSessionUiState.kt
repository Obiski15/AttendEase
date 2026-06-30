package com.example.attendease.state

import com.example.attendease.dto.response.AttendanceSessionResponse
import com.example.attendease.dto.response.AttendanceRecordResponse

data class LecturerSessionUiState(
    val activeSession: AttendanceSessionResponse? = null,
    val activeCourseTitle: String? = null,
    val locallyClosedSessionIds: Set<String> = emptySet(),
    val checkedInRecords: List<AttendanceRecordResponse> = emptyList(),
    val sessionsHistory: List<AttendanceSessionResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
