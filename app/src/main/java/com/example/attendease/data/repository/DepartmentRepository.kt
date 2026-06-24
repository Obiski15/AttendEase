package com.example.attendease.data.repository

import com.example.attendease.data.api.DepartmentApi
import com.example.attendease.dto.request.DepartmentCreateRequest
import com.example.attendease.dto.response.DepartmentResponse

class DepartmentRepository(
    private val departmentApi: DepartmentApi
) {
    suspend fun getDepartments(): List<DepartmentResponse> {
        return departmentApi.getDepartments()
    }

    suspend fun createDepartment(name: String): DepartmentResponse {
        val request = DepartmentCreateRequest(name = name)
        return departmentApi.createDepartment(request)
    }

    suspend fun deleteDepartment(departmentId: String) {
        departmentApi.deleteDepartment(departmentId)
    }
}
