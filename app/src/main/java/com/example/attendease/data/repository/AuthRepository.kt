package com.example.attendease.data.repository

import com.example.attendease.data.api.AuthApi
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.request.LoginRequest
import com.example.attendease.dto.response.LoginResponse
import com.example.attendease.dto.response.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) {
    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser = _currentUser.asStateFlow()

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse {

        val response = api.login(
            LoginRequest(
                email = email,
                password = password
            )
        )

        sessionManager.saveSession(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            role = response.user.role,
            name = response.user.name ?: "User",
            email = response.user.email ?: email
        )

        _currentUser.value = response.user

        return response
    }

    suspend fun getMe(forceRefresh: Boolean = false): UserResponse {
        val cached = _currentUser.value
        if (cached != null && !forceRefresh) {
            return cached
        }
        val user = api.getMe()
        _currentUser.value = user
        return user
    }

    fun clearCache() {
        _currentUser.value = null
    }
}