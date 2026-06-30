package com.example.attendease.data.repository

import android.util.Log
import com.example.attendease.data.api.DepartmentApi
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import com.example.attendease.dto.request.DepartmentCreateRequest
import com.example.attendease.dto.response.DepartmentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DepartmentRepository(
    private val departmentApi: DepartmentApi,
    private val apiCacheDao: ApiCacheDao
) {
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
            Log.w("DepartmentRepo", "Network failed, loading admin_departments cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("admin_departments") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
    }

    suspend fun createDepartment(name: String): DepartmentResponse {
        val request = DepartmentCreateRequest(name = name)
        return departmentApi.createDepartment(request)
    }

    suspend fun deleteDepartment(departmentId: String) {
        departmentApi.deleteDepartment(departmentId)
    }

    suspend fun getCachedDepartments(): List<DepartmentResponse>? {
        val cache = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { apiCacheDao.getApiCache("admin_departments") }
        return if (cache != null) kotlinx.serialization.json.Json.decodeFromString(cache.payloadJson) else null
    }

}