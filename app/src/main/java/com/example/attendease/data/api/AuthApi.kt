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
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class UnauthorizedException(message: String) : Exception(message)

class AuthApi(
    private val client: HttpClient = NetworkClient.client,
    private val sessionManager: SessionManager
) {
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
        val token = sessionManager.getAccessToken()
        val response = client.get("${this.url}/me") {
            if (token != null) {
                header("Authorization", "Bearer $token")
            }
        }

        if (response.status.value == 401) {
            // Access token expired, try to refresh
            val newTokens = try {
                refreshTokens()
            } catch (e: Exception) {
                sessionManager.clearSession()
                throw UnauthorizedException("Session expired")
            }

            // Retry the /me request with the new access token
            val retryResponse = client.get("${this.url}/me") {
                header("Authorization", "Bearer ${newTokens.accessToken}")
            }

            if (retryResponse.status.value in 200..299) {
                return retryResponse.body()
            } else {
                sessionManager.clearSession()
                throw UnauthorizedException("Session expired")
            }
        } else if (response.status.value in 200..299) {
            return response.body()
        } else {
            handleErrorResponse(response)
        }
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

    private suspend fun handleErrorResponse(response: io.ktor.client.statement.HttpResponse): Nothing {
        val errorText = try {
            response.bodyAsText()
        } catch (e: Exception) {
            ""
        }

        val errorMessage = try {
            val json = Json.parseToJsonElement(errorText).jsonObject
            json["message"]?.jsonPrimitive?.content
                ?: json["error"]?.jsonPrimitive?.content
                ?: json["detail"]?.jsonPrimitive?.content
                ?: "An error occurred (Status: ${response.status.value})"
        } catch (e: Exception) {
            if (errorText.isNotBlank()) errorText else "An error occurred (Status: ${response.status.value})"
        }

        throw Exception(errorMessage)
    }
}