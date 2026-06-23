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

    suspend fun createAcademicSession(sessionName: String, semester: String, isActive: Boolean): AcademicSessionResponse {
        val request = AcademicSessionCreateRequest(
            sessionName = sessionName,
            semester = semester,
            isActive = isActive
        )
        return academicSessionApi.createAcademicSession(request)
    }

    suspend fun activateAcademicSession(sessionId: String): AcademicSessionResponse {
        return academicSessionApi.activateAcademicSession(sessionId)
    }

    suspend fun deleteAcademicSession(sessionId: String) {
        academicSessionApi.deleteAcademicSession(sessionId)
    }
}
