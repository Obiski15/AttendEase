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
        return try {
            val response = courseApi.getCourses()
            withContext(Dispatchers.IO) {
                apiCacheDao.insertApiCache(
                    ApiCacheEntity(
                        cacheKey = "admin_courses",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("CourseAssignmentRepo", "Network failed, loading admin_courses cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("admin_courses") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
    }

    suspend fun searchLecturers(query: String? = null, skip: Int = 0, limit: Int = 20): com.example.attendease.dto.response.PaginatedResponse<LecturerResponse> {
        return try {
            val response = lecturerApi.getLecturers(skip, limit, query)
            if (skip == 0 && (query == null || query.isBlank())) {
                withContext(Dispatchers.IO) {
                    apiCacheDao.insertApiCache(
                        ApiCacheEntity(
                            cacheKey = "admin_lecturers_first_page",
                            payloadJson = Json.encodeToString(response)
                        )
                    )
                }
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            if (skip == 0 && (query == null || query.isBlank())) {
                Log.w("CourseAssignmentRepo", "Network failed, loading admin_lecturers_first_page cache", e)
                val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("admin_lecturers_first_page") }
                if (cache != null) {
                    Json.decodeFromString(cache.payloadJson)
                } else {
                    throw e
                }
            } else {
                throw e
            }
        }
    }

    suspend fun getAcademicSessions(): List<AcademicSessionResponse> {
        return try {
            val response = academicSessionApi.getAcademicSessions()
            withContext(Dispatchers.IO) {
                apiCacheDao.insertApiCache(
                    ApiCacheEntity(
                        cacheKey = "admin_academic_sessions",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("CourseAssignmentRepo", "Network failed, loading admin_academic_sessions cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("admin_academic_sessions") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
    }

    suspend fun getCourseAssignments(): List<CourseAssignmentResponse> {
        return try {
            val response = courseAssignmentApi.getCourseAssignments()
            withContext(Dispatchers.IO) {
                apiCacheDao.insertApiCache(
                    ApiCacheEntity(
                        cacheKey = "admin_course_assignments",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
            Log.w("CourseAssignmentRepo", "Network failed, loading admin_course_assignments cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("admin_course_assignments") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
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
