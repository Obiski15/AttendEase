package com.example.attendease.data.repository

import com.example.attendease.data.api.CourseApi
import com.example.attendease.data.api.LecturerApi
import com.example.attendease.data.api.AcademicSessionApi
import com.example.attendease.data.api.CourseAssignmentApi
import com.example.attendease.dto.request.CourseAssignmentCreateRequest
import com.example.attendease.dto.response.CourseResponse
import com.example.attendease.dto.response.LecturerResponse
import com.example.attendease.dto.response.AcademicSessionResponse
import com.example.attendease.dto.response.CourseAssignmentResponse

class CourseAssignmentRepository(
    private val courseApi: CourseApi,
    private val lecturerApi: LecturerApi,
    private val academicSessionApi: AcademicSessionApi,
    private val courseAssignmentApi: CourseAssignmentApi
) {
    suspend fun getCourses(): List<CourseResponse> {
        return courseApi.getCourses()
    }

    suspend fun searchLecturers(query: String? = null, skip: Int = 0, limit: Int = 20): com.example.attendease.dto.response.PaginatedResponse<LecturerResponse> {
        return lecturerApi.getLecturers(skip, limit, query)
    }

    suspend fun getAcademicSessions(): List<AcademicSessionResponse> {
        return academicSessionApi.getAcademicSessions()
    }

    suspend fun getCourseAssignments(): List<CourseAssignmentResponse> {
        return courseAssignmentApi.getCourseAssignments()
    }

    suspend fun createCourseAssignment(
        courseId: String,
        lecturerId: String,
        academicSessionId: String
    ): CourseAssignmentResponse {
        val request = CourseAssignmentCreateRequest(
            courseId = courseId,
            lecturerId = lecturerId,
            academicSessionId = academicSessionId
        )
        return courseAssignmentApi.createCourseAssignment(request)
    }

    suspend fun deleteCourseAssignment(assignmentId: String): CourseAssignmentResponse {
        return courseAssignmentApi.deleteCourseAssignment(assignmentId)
    }
}
