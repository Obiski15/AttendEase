package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.request.AttendanceCheckInRequest
import com.example.attendease.dto.response.AttendanceRecordResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class AttendanceApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager,
    authApi: AuthApi
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { authApi }
    }

    private val url = "${BuildConfig.BASE_URL}/attendance"

    suspend fun checkIn(request: AttendanceCheckInRequest): AttendanceRecordResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/check-in", request)
    }

    suspend fun getMyAttendance(): List<AttendanceRecordResponse> {
        return authenticatedRequest(HttpMethod.Get, "$url/me")
    }
}
