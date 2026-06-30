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
        return try {
            val response = studentApi.getStudents(skip, limit)
            if (skip == 0) {
                withContext(Dispatchers.IO) {
                    apiCacheDao.insertApiCache(
                        ApiCacheEntity(
                            cacheKey = "admin_students_first_page",
                            payloadJson = Json.encodeToString(response)
                        )
                    )
                }
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            if (skip == 0) {
                Log.w("StudentRepo", "Network failed, loading admin_students_first_page cache", e)
                val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("admin_students_first_page") }
                if (cache != null) {
                    Json.decodeFromString(cache.payloadJson)
                } else {
                    throw e
                }
            } else {
                throw e
            }
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
        return try {
            val response = departmentApi.getDepartments()
            withContext(Dispatchers.IO) {
                apiCacheDao.insertApiCache(
                    ApiCacheEntity(
                        cacheKey = "admin_departments",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("StudentRepo", "Network failed, loading admin_departments cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("admin_departments") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
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