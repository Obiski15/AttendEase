package com.example.attendease.data.repository

import com.example.attendease.data.api.UserApi
import com.example.attendease.dto.request.UserCreateRequest
import com.example.attendease.dto.request.UserUpdateRequest
import com.example.attendease.dto.response.UserResponse

class UserRepository(
    private val userApi: UserApi
) {
    suspend fun getUsers(): List<UserResponse> {
        return userApi.getUsers()
    }

    suspend fun getUser(userId: String): UserResponse {
        return userApi.getUser(userId)
    }

    suspend fun createUser(request: UserCreateRequest): UserResponse {
        return userApi.createUser(request)
    }

    suspend fun updateUser(userId: String, request: UserUpdateRequest): UserResponse {
        return userApi.updateUser(userId, request)
    }

    suspend fun deleteUser(userId: String): UserResponse {
        return userApi.deleteUser(userId)
    }
}
