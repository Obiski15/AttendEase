package com.example.attendease.data.repository

import android.util.Log
import com.example.attendease.data.api.CourseApi
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import com.example.attendease.dto.request.CourseCreateRequest
import com.example.attendease.dto.response.CourseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CourseRepository(
    private val courseApi: CourseApi,
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
            Log.w("CourseRepo", "Network failed, loading admin_courses cache", e)
            val cache = withContext(Dispatchers.IO) { apiCacheDao.getApiCache("admin_courses") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
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

    suspend fun getCachedCourses(): List<CourseResponse>? {
        val cache = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { apiCacheDao.getApiCache("admin_courses") }
        return if (cache != null) kotlinx.serialization.json.Json.decodeFromString(cache.payloadJson) else null
    }

}