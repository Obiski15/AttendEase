package com.example.attendease.data.repository

import com.example.attendease.data.api.AttendanceSessionApi
import com.example.attendease.dto.request.AttendanceSessionCreateRequest
import com.example.attendease.dto.response.AttendanceRecordResponse
import com.example.attendease.dto.response.AttendanceSessionResponse

class AttendanceSessionRepository(
    private val attendanceSessionApi: AttendanceSessionApi
) {
    suspend fun openSession(request: AttendanceSessionCreateRequest): AttendanceSessionResponse {
        return attendanceSessionApi.openSession(request)
    }

    suspend fun closeSession(sessionId: String): AttendanceSessionResponse {
        return attendanceSessionApi.closeSession(sessionId)
    }

    suspend fun getSessionRecords(sessionId: String): List<AttendanceRecordResponse> {
        return attendanceSessionApi.getSessionRecords(sessionId)
    }

    suspend fun getAttendanceSessions(): List<AttendanceSessionResponse> {
        return attendanceSessionApi.getAttendanceSessions()
    }
}
