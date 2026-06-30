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
        return withCache(
            cacheKey = "admin_departments",
            apiCacheDao = apiCacheDao,
            refresh = true,
            logTag = this::class.simpleName ?: "DepartmentRepository"
        ) {
            departmentApi.getDepartments()
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