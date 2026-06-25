package com.example.attendease.data.repository

import android.util.Log
import com.example.attendease.data.api.UserApi
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import com.example.attendease.dto.request.UserCreateRequest
import com.example.attendease.dto.request.UserUpdateRequest
import com.example.attendease.dto.response.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserRepository(
    private val userApi: UserApi,
    private val apiCacheDao: ApiCacheDao
) {
    suspend fun getUsers(): List<UserResponse> {
        return userApi.getUsers()
    }

    suspend fun getUser(userId: String): UserResponse {
        return try {
            val response = userApi.getUser(userId)
            withContext(Dispatchers.IO) {
                apiCacheDao.insertApiCache(
                    ApiCacheEntity(
                        cacheKey = "user_$userId",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            Log.w("UserRepo", "Network failed, loading user cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("user_$userId") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
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
