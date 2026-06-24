package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.request.UserCreateRequest
import com.example.attendease.dto.request.UserUpdateRequest
import com.example.attendease.dto.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class UserApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager,
    authApi: AuthApi
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { authApi }
    }

    private val url = "${BuildConfig.BASE_URL}/users"

    suspend fun getUsers(): List<UserResponse> {
        return authenticatedRequest(HttpMethod.Get, "$url/")
    }

    suspend fun getUser(userId: String): UserResponse {
        return authenticatedRequest(HttpMethod.Get, "$url/$userId")
    }

    suspend fun createUser(request: UserCreateRequest): UserResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/", request)
    }

    suspend fun updateUser(userId: String, request: UserUpdateRequest): UserResponse {
        return authenticatedRequest(HttpMethod.Patch, "$url/$userId", request)
    }

    suspend fun deleteUser(userId: String): UserResponse {
        return authenticatedRequest(HttpMethod.Delete, "$url/$userId")
    }
}
