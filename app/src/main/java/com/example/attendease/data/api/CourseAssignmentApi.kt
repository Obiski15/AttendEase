package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.request.CourseAssignmentCreateRequest
import com.example.attendease.dto.response.CourseAssignmentResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class CourseAssignmentApi(
    client: HttpClient = NetworkClient.client,
    sessionManager: SessionManager,
    authApi: AuthApi
) : BaseApi(client, sessionManager) {
    init {
        authApiProvider = { authApi }
    }

    private val url = "${BuildConfig.BASE_URL}/course-assignments"

    suspend fun getCourseAssignments(): List<CourseAssignmentResponse> {
        return authenticatedRequest(HttpMethod.Get, "$url/")
    }

    suspend fun createCourseAssignment(request: CourseAssignmentCreateRequest): CourseAssignmentResponse {
        return authenticatedRequest(HttpMethod.Post, "$url/", request)
    }

    suspend fun deleteCourseAssignment(assignmentId: String) {
        authenticatedRequest<Unit>(HttpMethod.Delete, "$url/$assignmentId")
    }
}
