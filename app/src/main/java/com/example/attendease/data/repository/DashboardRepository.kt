package com.example.attendease.data.repository

import com.example.attendease.data.api.DashboardApi
import com.example.attendease.dto.response.AdminDashboardResponse

class DashboardRepository(
    private val dashboardApi: DashboardApi
) {
    suspend fun getAdminDashboard(): AdminDashboardResponse {
        return dashboardApi.getAdminDashboard()
    }
}
