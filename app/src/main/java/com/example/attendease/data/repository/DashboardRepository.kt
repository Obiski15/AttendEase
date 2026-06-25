package com.example.attendease.data.repository

import android.util.Log
import com.example.attendease.data.api.DashboardApi
import com.example.attendease.data.local.dao.DashboardDao
import com.example.attendease.data.local.entity.DashboardCacheEntity
import com.example.attendease.dto.response.AdminDashboardResponse
import com.example.attendease.dto.response.LecturerDashboardResponse
import com.example.attendease.dto.response.StudentDashboardResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DashboardRepository(
    private val dashboardApi: DashboardApi,
    private val dashboardDao: DashboardDao
) {
    suspend fun getAdminDashboard(): AdminDashboardResponse {
        return try {
            val response = dashboardApi.getAdminDashboard()
            withContext(Dispatchers.IO) {
                dashboardDao.insertDashboardCache(
                    DashboardCacheEntity(
                        role = "ADMIN",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            Log.w("DashboardRepo", "Network failed, loading ADMIN cache", e)
            val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("ADMIN") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
    }

    suspend fun getLecturerDashboard(): LecturerDashboardResponse {
        return try {
            val response = dashboardApi.getLecturerDashboard()
            withContext(Dispatchers.IO) {
                dashboardDao.insertDashboardCache(
                    DashboardCacheEntity(
                        role = "LECTURER",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            Log.w("DashboardRepo", "Network failed, loading LECTURER cache", e)
            val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("LECTURER") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
    }

    suspend fun getStudentDashboard(): StudentDashboardResponse {
        return try {
            val response = dashboardApi.getStudentDashboard()
            withContext(Dispatchers.IO) {
                dashboardDao.insertDashboardCache(
                    DashboardCacheEntity(
                        role = "STUDENT",
                        payloadJson = Json.encodeToString(response)
                    )
                )
            }
            response
        } catch (e: Exception) {
            Log.w("DashboardRepo", "Network failed, loading STUDENT cache", e)
            val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("STUDENT") }
            if (cache != null) {
                Json.decodeFromString(cache.payloadJson)
            } else {
                throw e
            }
        }
    }

    suspend fun getCachedAdminDashboard(): AdminDashboardResponse? {
        val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("ADMIN") }
        return if (cache != null) Json.decodeFromString(cache.payloadJson) else null
    }

    suspend fun getCachedLecturerDashboard(): LecturerDashboardResponse? {
        val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("LECTURER") }
        return if (cache != null) Json.decodeFromString(cache.payloadJson) else null
    }

    suspend fun getCachedStudentDashboard(): StudentDashboardResponse? {
        val cache = withContext(Dispatchers.IO) { dashboardDao.getDashboardCache("STUDENT") }
        return if (cache != null) Json.decodeFromString(cache.payloadJson) else null
    }
}
