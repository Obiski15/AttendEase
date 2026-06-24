package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.response.AdminDashboardResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class DashboardApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager,
    authApi: AuthApi
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { authApi }
    }

    private val url = "${BuildConfig.BASE_URL}/dashboard"

    suspend fun getAdminDashboard(): AdminDashboardResponse {
        return authenticatedRequest(HttpMethod.Get, "$url/admin")
    }
}
