package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.request.CourseCreateRequest
import com.example.attendease.dto.response.CourseResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class CourseApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager,
    authApi: AuthApi
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { authApi }
    }

    private val url = "${BuildConfig.BASE_URL}/courses"

    suspend fun getCourses(): List<CourseResponse> {
        return authenticatedRequest(HttpMethod.Get, "$url/")
    }

    suspend fun createCourse(request: CourseCreateRequest): CourseResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/", request)
    }
}
