package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.request.LecturerCreateRequest
import com.example.attendease.dto.request.LecturerUpdateRequest
import com.example.attendease.dto.response.LecturerResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class LecturerApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager,
    authApi: AuthApi
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { authApi }
    }

    private val url = "${BuildConfig.BASE_URL}/lecturers"

    suspend fun getLecturers(
        skip: Int = 0,
        limit: Int = 100,
        search: String? = null
    ): com.example.attendease.dto.response.PaginatedResponse<LecturerResponse> {
        val searchParam = search?.let { "&search=${java.net.URLEncoder.encode(it, "UTF-8")}" } ?: ""
        return authenticatedRequest(HttpMethod.Get, "$url/?skip=$skip&limit=$limit$searchParam")
    }

    suspend fun getLecturer(userId: String): LecturerResponse {
        return authenticatedRequest(HttpMethod.Get, "$url/$userId")
    }

    suspend fun createLecturer(request: LecturerCreateRequest): LecturerResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/", request)
    }

    suspend fun updateLecturer(userId: String, request: LecturerUpdateRequest): LecturerResponse {
        return authenticatedRequest(HttpMethod.Patch, "$url/$userId", request)
    }

    suspend fun deleteLecturer(userId: String) {
        authenticatedRequest<Unit>(HttpMethod.Delete, "$url/$userId")
    }
}
