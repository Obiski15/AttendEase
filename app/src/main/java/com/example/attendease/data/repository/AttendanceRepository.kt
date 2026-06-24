package com.example.attendease.data.repository

import com.example.attendease.data.api.AttendanceApi
import com.example.attendease.dto.request.AttendanceCheckInRequest
import com.example.attendease.dto.response.AttendanceRecordResponse

class AttendanceRepository(
    private val attendanceApi: AttendanceApi
) {
    suspend fun checkIn(request: AttendanceCheckInRequest): AttendanceRecordResponse {
        return attendanceApi.checkIn(request)
    }

    suspend fun getMyAttendance(): List<AttendanceRecordResponse> {
        return attendanceApi.getMyAttendance()
    }
}
