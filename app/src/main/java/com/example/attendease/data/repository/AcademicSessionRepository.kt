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
        return try {
            val response = academicSessionApi.getAcademicSessions()
            withContext(Dispatchers.IO) {
                apiCacheDao.insertApiCache(
                    ApiCacheEntity(
                        cacheKey = "admin_academic_sessions",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("AcademicSessionRepo", "Network failed, loading admin_academic_sessions cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("admin_academic_sessions") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
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