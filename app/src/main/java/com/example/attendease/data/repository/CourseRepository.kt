package com.example.attendease.data.repository

import com.example.attendease.data.api.CourseApi
import com.example.attendease.dto.request.CourseCreateRequest
import com.example.attendease.dto.response.CourseResponse

class CourseRepository(
    private val courseApi: CourseApi
) {
    suspend fun getCourses(): List<CourseResponse> {
        return courseApi.getCourses()
    }

    suspend fun createCourse(title: String, courseCode: String, creditUnits: Int, departmentId: String): CourseResponse {
        val request = CourseCreateRequest(
            title = title,
            courseCode = courseCode,
            creditUnits = creditUnits,
            departmentId = departmentId
        )
        return courseApi.createCourse(request)
    }

    suspend fun getCourse(courseId: String): CourseResponse {
        return courseApi.getCourse(courseId)
    }

    suspend fun updateCourse(courseId: String, title: String, courseCode: String, creditUnits: Int, departmentId: String): CourseResponse {
        val request = com.example.attendease.dto.request.CourseUpdateRequest(
            title = title,
            courseCode = courseCode,
            creditUnits = creditUnits,
            departmentId = departmentId
        )
        return courseApi.updateCourse(courseId, request)
    }

    suspend fun deleteCourse(courseId: String) {
        courseApi.deleteCourse(courseId)
    }
}
