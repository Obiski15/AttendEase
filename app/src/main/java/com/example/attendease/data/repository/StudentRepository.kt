package com.example.attendease.data.repository

import com.example.attendease.data.api.DepartmentApi
import com.example.attendease.data.api.StudentApi
import com.example.attendease.dto.request.StudentCreateRequest
import com.example.attendease.dto.request.StudentUpdateRequest
import com.example.attendease.dto.response.DepartmentResponse
import com.example.attendease.dto.response.StudentResponse

class StudentRepository(
    private val studentApi: StudentApi,
    private val departmentApi: DepartmentApi
) {
    suspend fun getStudents(): List<StudentResponse> {
        return studentApi.getStudents()
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
        return departmentApi.getDepartments()
    }
}
