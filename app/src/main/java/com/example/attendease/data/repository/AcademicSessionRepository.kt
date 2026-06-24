package com.example.attendease.data.repository

import com.example.attendease.data.api.AcademicSessionApi
import com.example.attendease.dto.request.AcademicSessionCreateRequest
import com.example.attendease.dto.response.AcademicSessionResponse

class AcademicSessionRepository(
    private val academicSessionApi: AcademicSessionApi
) {
    suspend fun getAcademicSessions(): List<AcademicSessionResponse> {
        return academicSessionApi.getAcademicSessions()
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

}
