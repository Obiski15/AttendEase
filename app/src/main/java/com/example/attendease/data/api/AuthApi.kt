package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.request.LoginRequest
import com.example.attendease.dto.request.TokenRefreshRequest
import com.example.attendease.dto.response.LoginResponse
import com.example.attendease.dto.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

class UnauthorizedException(message: String) : Exception(message)

class AuthApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { this }
    }

    private val url = "${BuildConfig.BASE_URL}/auth"

    suspend fun login(request: LoginRequest): LoginResponse {
        val response = client.post("${this.url}/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (response.status.value in 200..299) {
            return response.body()
        } else {
            handleErrorResponse(response)
        }
    }

    suspend fun getMe(): UserResponse {
        return authenticatedRequest(HttpMethod.Get, "$url/me")
    }

    suspend fun refreshTokens(): LoginResponse {
        val refreshToken = sessionManager.getRefreshToken() ?: throw Exception("No refresh token available")
        val response = client.post("${this.url}/refresh") {
            contentType(ContentType.Application.Json)
            setBody(TokenRefreshRequest(refreshToken))
        }

        if (response.status.value in 200..299) {
            val loginResponse = response.body<LoginResponse>()
            sessionManager.saveSession(
                accessToken = loginResponse.accessToken,
                refreshToken = loginResponse.refreshToken,
                role = loginResponse.user.role,
                name = loginResponse.user.name ?: sessionManager.getUserName(),
                email = loginResponse.user.email ?: sessionManager.getUserEmail()
            )
            return loginResponse
        } else {
            handleErrorResponse(response)
        }
    }
}