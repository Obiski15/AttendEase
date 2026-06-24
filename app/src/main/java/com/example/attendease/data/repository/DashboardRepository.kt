package com.example.attendease.data.repository

import com.example.attendease.data.api.DashboardApi
import com.example.attendease.dto.response.AdminDashboardResponse
import com.example.attendease.dto.response.LecturerDashboardResponse
import com.example.attendease.dto.response.StudentDashboardResponse

class DashboardRepository(
    private val dashboardApi: DashboardApi
) {
    suspend fun getAdminDashboard(): AdminDashboardResponse {
        return dashboardApi.getAdminDashboard()
    }

    suspend fun getLecturerDashboard(): LecturerDashboardResponse {
        return dashboardApi.getLecturerDashboard()
    }

    suspend fun getStudentDashboard(): StudentDashboardResponse {
        return dashboardApi.getStudentDashboard()
    }
}
