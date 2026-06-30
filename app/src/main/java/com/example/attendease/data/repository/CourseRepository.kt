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
        return withCache(
            cacheKey = "admin_courses",
            apiCacheDao = apiCacheDao,
            refresh = true,
            logTag = this::class.simpleName ?: "CourseRepository"
        ) {
            courseApi.getCourses()
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