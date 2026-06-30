package com.example.attendease.data.repository

import android.util.Log
import com.example.attendease.data.api.DepartmentApi
import com.example.attendease.data.api.LecturerApi
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import com.example.attendease.dto.request.LecturerCreateRequest
import com.example.attendease.dto.request.LecturerUpdateRequest
import com.example.attendease.dto.response.DepartmentResponse
import com.example.attendease.dto.response.LecturerResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LecturerRepository(
    private val lecturerApi: LecturerApi,
    private val departmentApi: DepartmentApi,
    private val apiCacheDao: ApiCacheDao
) {
    suspend fun getLecturers(skip: Int = 0, limit: Int = 100): com.example.attendease.dto.response.PaginatedResponse<LecturerResponse> {
        return if (skip == 0) {
            withCache(
                cacheKey = "admin_lecturers_first_page",
                apiCacheDao = apiCacheDao,
                refresh = true,
                logTag = this::class.simpleName ?: "LecturerRepository"
            ) {
                lecturerApi.getLecturers(skip, limit)
            }
        } else {
            lecturerApi.getLecturers(skip, limit)
        }
    }

    suspend fun getLecturer(userId: String): LecturerResponse {
        return lecturerApi.getLecturer(userId)
    }

    suspend fun createLecturer(request: LecturerCreateRequest): LecturerResponse {
        return lecturerApi.createLecturer(request)
    }

    suspend fun updateLecturer(userId: String, request: LecturerUpdateRequest): LecturerResponse {
        return lecturerApi.updateLecturer(userId, request)
    }

    suspend fun deleteLecturer(userId: String) {
        lecturerApi.deleteLecturer(userId)
    }

    suspend fun getDepartments(): List<DepartmentResponse> {
        return withCache(
            cacheKey = "admin_departments",
            apiCacheDao = apiCacheDao,
            refresh = true,
            logTag = this::class.simpleName ?: "LecturerRepository"
        ) {
            departmentApi.getDepartments()
        }
    }

    suspend fun getCachedLecturers(): com.example.attendease.dto.response.PaginatedResponse<LecturerResponse>? {
        val cache = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { apiCacheDao.getApiCache("admin_lecturers_first_page") }
        return if (cache != null) kotlinx.serialization.json.Json.decodeFromString(cache.payloadJson) else null
    }

    suspend fun getCachedDepartments(): List<DepartmentResponse>? {
        val cache = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { apiCacheDao.getApiCache("admin_departments") }
        return if (cache != null) kotlinx.serialization.json.Json.decodeFromString(cache.payloadJson) else null
    }

}