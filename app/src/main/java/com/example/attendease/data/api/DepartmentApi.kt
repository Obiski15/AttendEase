package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.response.DepartmentResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class DepartmentApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager,
    authApi: AuthApi
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { authApi }
    }

    private val url = "${BuildConfig.BASE_URL}/departments"

    suspend fun getDepartments(): List<DepartmentResponse> {
        return authenticatedRequest(HttpMethod.Get, "$url/")
    }

    suspend fun createDepartment(request: com.example.attendease.dto.request.DepartmentCreateRequest): DepartmentResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/", request)
    }

    suspend fun deleteDepartment(departmentId: String) {
        authenticatedRequest<Unit>(HttpMethod.Delete, "$url/$departmentId")
    }
}
