package com.example.attendease.data.repository

import android.util.Log
import com.example.attendease.data.api.DepartmentApi
import com.example.attendease.data.api.StudentApi
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import com.example.attendease.dto.request.StudentCreateRequest
import com.example.attendease.dto.request.StudentUpdateRequest
import com.example.attendease.dto.response.DepartmentResponse
import com.example.attendease.dto.response.StudentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StudentRepository(
    private val studentApi: StudentApi,
    private val departmentApi: DepartmentApi,
    private val apiCacheDao: ApiCacheDao
) {
    suspend fun getStudents(skip: Int = 0, limit: Int = 100): com.example.attendease.dto.response.PaginatedResponse<StudentResponse> {
        return if (skip == 0) {
            withCache(
                cacheKey = "admin_students_first_page",
                apiCacheDao = apiCacheDao,
                refresh = true,
                logTag = this::class.simpleName ?: "StudentRepository"
            ) {
                studentApi.getStudents(skip, limit)
            }
        } else {
            studentApi.getStudents(skip, limit)
        }
    }

    suspend fun getStudent(userId: String): StudentResponse {
        return studentApi.getStudent(userId)
    }

    suspend fun createStudent(request: StudentCreateRequest): StudentResponse {
        return studentApi.createStudent(request)
    }

    suspend fun updateStudent(userId: String, request: StudentUpdateRequest): StudentResponse {
        return studentApi.updateStudent(userId, request)
    }

    suspend fun deleteStudent(userId: String) {
        studentApi.deleteStudent(userId)
    }

    suspend fun getDepartments(): List<DepartmentResponse> {
        return withCache(
            cacheKey = "admin_departments",
            apiCacheDao = apiCacheDao,
            refresh = true,
            logTag = this::class.simpleName ?: "StudentRepository"
        ) {
            departmentApi.getDepartments()
        }
    }

    suspend fun getCachedStudents(): com.example.attendease.dto.response.PaginatedResponse<StudentResponse>? {
        val cache = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { apiCacheDao.getApiCache("admin_students_first_page") }
        return if (cache != null) kotlinx.serialization.json.Json.decodeFromString(cache.payloadJson) else null
    }

    suspend fun getCachedDepartments(): List<DepartmentResponse>? {
        val cache = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { apiCacheDao.getApiCache("admin_departments") }
        return if (cache != null) kotlinx.serialization.json.Json.decodeFromString(cache.payloadJson) else null
    }

}