package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.request.StudentCreateRequest
import com.example.attendease.dto.request.StudentUpdateRequest
import com.example.attendease.dto.response.StudentResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class StudentApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager,
    authApi: AuthApi
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { authApi }
    }

    private val url = "${BuildConfig.BASE_URL}/students"

    suspend fun getStudents(): List<StudentResponse> {
        return authenticatedRequest(HttpMethod.Get, "$url/")
    }

    suspend fun getStudent(userId: String): StudentResponse {
        return authenticatedRequest(HttpMethod.Get, "$url/$userId")
    }

    suspend fun createStudent(request: StudentCreateRequest): StudentResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/", request)
    }

    suspend fun updateStudent(userId: String, request: StudentUpdateRequest): StudentResponse {
        return authenticatedRequest(HttpMethod.Patch, "$url/$userId", request)
    }

    suspend fun deleteStudent(userId: String) {
        authenticatedRequest<Unit>(HttpMethod.Delete, "$url/$userId")
    }
}
