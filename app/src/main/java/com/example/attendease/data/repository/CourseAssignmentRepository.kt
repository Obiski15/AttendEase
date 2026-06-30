package com.example.attendease.data.repository

import android.util.Log
import com.example.attendease.data.api.CourseApi
import com.example.attendease.data.api.LecturerApi
import com.example.attendease.data.api.AcademicSessionApi
import com.example.attendease.data.api.CourseAssignmentApi
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import com.example.attendease.dto.request.CourseAssignmentCreateRequest
import com.example.attendease.dto.response.CourseResponse
import com.example.attendease.dto.response.LecturerResponse
import com.example.attendease.dto.response.AcademicSessionResponse
import com.example.attendease.dto.response.CourseAssignmentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CourseAssignmentRepository(
    private val courseApi: CourseApi,
    private val lecturerApi: LecturerApi,
    private val academicSessionApi: AcademicSessionApi,
    private val courseAssignmentApi: CourseAssignmentApi,
    private val apiCacheDao: ApiCacheDao
) {
    suspend fun getCourses(): List<CourseResponse> {
        return withCache(
            cacheKey = "admin_courses",
            apiCacheDao = apiCacheDao,
            refresh = true,
            logTag = this::class.simpleName ?: "CourseAssignmentRepository"
        ) {
            courseApi.getCourses()
        }
    }

    suspend fun searchLecturers(query: String? = null, skip: Int = 0, limit: Int = 20): com.example.attendease.dto.response.PaginatedResponse<LecturerResponse> {
        return if (skip == 0 && (query == null || query.isBlank())) {
            withCache(
                cacheKey = "admin_lecturers_first_page",
                apiCacheDao = apiCacheDao,
                refresh = true,
                logTag = this::class.simpleName ?: "CourseAssignmentRepository"
            ) {
                lecturerApi.getLecturers(skip, limit, query)
            }
        } else {
            lecturerApi.getLecturers(skip, limit, query)
        }
    }

    suspend fun getAcademicSessions(): List<AcademicSessionResponse> {
        return withCache(
            cacheKey = "admin_academic_sessions",
            apiCacheDao = apiCacheDao,
            refresh = true,
            logTag = this::class.simpleName ?: "CourseAssignmentRepository"
        ) {
            academicSessionApi.getAcademicSessions()
        }
    }

    suspend fun getCourseAssignments(): List<CourseAssignmentResponse> {
        return withCache(
            cacheKey = "admin_course_assignments",
            apiCacheDao = apiCacheDao,
            refresh = true,
            logTag = this::class.simpleName ?: "CourseAssignmentRepository"
        ) {
            courseAssignmentApi.getCourseAssignments()
        }
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
