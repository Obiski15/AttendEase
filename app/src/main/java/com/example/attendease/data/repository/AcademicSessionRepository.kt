package com.example.attendease.data.repository

import android.util.Log
import com.example.attendease.data.api.AcademicSessionApi
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import com.example.attendease.dto.request.AcademicSessionCreateRequest
import com.example.attendease.dto.response.AcademicSessionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AcademicSessionRepository(
    private val academicSessionApi: AcademicSessionApi,
    private val apiCacheDao: ApiCacheDao
) {
    suspend fun getAcademicSessions(): List<AcademicSessionResponse> {
        return withCache(
            cacheKey = "admin_academic_sessions",
            apiCacheDao = apiCacheDao,
            refresh = true,
            logTag = this::class.simpleName ?: "AcademicSessionRepository"
        ) {
            academicSessionApi.getAcademicSessions()
        }
    }

    suspend fun createAcademicSession(
        sessionName: String,
        semester: String,
        isActive: Boolean,
        startDate: String,
        endDate: String
    ): AcademicSessionResponse {
        val request = AcademicSessionCreateRequest(
            sessionName = sessionName,
            semester = semester,
            isActive = isActive,
            startDate = startDate,
            endDate = endDate
        )
        return academicSessionApi.createAcademicSession(request)
    }

    suspend fun activateAcademicSession(sessionId: String): AcademicSessionResponse {
        return academicSessionApi.activateAcademicSession(sessionId)
    }

    suspend fun updateAcademicSession(
        sessionId: String,
        sessionName: String?,
        semester: String?,
        isActive: Boolean?,
        startDate: String? = null,
        endDate: String? = null
    ): AcademicSessionResponse {
        val request = com.example.attendease.dto.request.AcademicSessionUpdateRequest(
            sessionName = sessionName,
            semester = semester,
            isActive = isActive,
            startDate = startDate,
            endDate = endDate
        )
        return academicSessionApi.updateAcademicSession(sessionId, request)
    }

    suspend fun deleteAcademicSession(sessionId: String) {
        academicSessionApi.deleteAcademicSession(sessionId)
    }

    suspend fun getCachedAcademicSessions(): List<AcademicSessionResponse>? {
        val cache = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { apiCacheDao.getApiCache("admin_academic_sessions") }
        return if (cache != null) kotlinx.serialization.json.Json.decodeFromString(cache.payloadJson) else null
    }

}