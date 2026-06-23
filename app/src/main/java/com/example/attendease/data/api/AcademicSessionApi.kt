package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.response.AcademicSessionResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class AcademicSessionApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager,
    authApi: AuthApi
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { authApi }
    }

    private val url = "${BuildConfig.BASE_URL}/academic-sessions"

    suspend fun getAcademicSessions(): List<AcademicSessionResponse> {
        return authenticatedRequest(HttpMethod.Get, "$url/")
    }

    suspend fun createAcademicSession(request: com.example.attendease.dto.request.AcademicSessionCreateRequest): AcademicSessionResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/", request)
    }

    suspend fun activateAcademicSession(sessionId: String): AcademicSessionResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/$sessionId/activate")
    }

    suspend fun deleteAcademicSession(sessionId: String) {
        authenticatedRequest<Unit>(HttpMethod.Delete, "$url/$sessionId")
    }
}
