package com.example.attendease.data.repository

import com.example.attendease.data.api.DepartmentApi
import com.example.attendease.data.api.LecturerApi
import com.example.attendease.dto.request.LecturerCreateRequest
import com.example.attendease.dto.request.LecturerUpdateRequest
import com.example.attendease.dto.response.DepartmentResponse
import com.example.attendease.dto.response.LecturerResponse

class LecturerRepository(
    private val lecturerApi: LecturerApi,
    private val departmentApi: DepartmentApi
) {
    suspend fun getLecturers(): List<LecturerResponse> {
        return lecturerApi.getLecturers()
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
        return departmentApi.getDepartments()
    }
}
