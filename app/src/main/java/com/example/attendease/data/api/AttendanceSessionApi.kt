package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.request.AttendanceSessionCreateRequest
import com.example.attendease.dto.response.AttendanceRecordResponse
import com.example.attendease.dto.response.AttendanceSessionResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class AttendanceSessionApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager,
    authApi: AuthApi
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { authApi }
    }

    private val url = "${BuildConfig.BASE_URL}/attendance-sessions"

    suspend fun openSession(request: AttendanceSessionCreateRequest): AttendanceSessionResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/", request)
    }

    suspend fun closeSession(sessionId: String): AttendanceSessionResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/$sessionId/close")
    }

    suspend fun getSessionRecords(sessionId: String): List<AttendanceRecordResponse> {
        return authenticatedRequest(HttpMethod.Get, "$url/$sessionId/records")
    }

    suspend fun getAttendanceSessions(skip: Int = 0, limit: Int = 100): com.example.attendease.dto.response.PaginatedResponse<AttendanceSessionResponse> {
        return authenticatedRequest(HttpMethod.Get, "$url/?skip=$skip&limit=$limit")
    }
}
