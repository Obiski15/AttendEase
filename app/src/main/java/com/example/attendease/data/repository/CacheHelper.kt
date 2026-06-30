package com.example.attendease.data.repository

import android.util.Log
import com.example.attendease.data.local.dao.ApiCacheDao
import com.example.attendease.data.local.entity.ApiCacheEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend inline fun <reified T> withCache(
    cacheKey: String,
    apiCacheDao: ApiCacheDao,
    refresh: Boolean = false,
    logTag: String = "Repository",
    crossinline apiCall: suspend () -> T
): T {
    val cached = withContext(Dispatchers.IO) { apiCacheDao.getApiCache(cacheKey) }
    if (cached != null && !refresh) {
        try {
            return Json.decodeFromString<T>(cached.payloadJson)
        } catch (e: Exception) {
            Log.e(logTag, "Failed to deserialize cached data for $cacheKey", e)
        }
    }
    return try {
        val response = apiCall()
        withContext(Dispatchers.IO) {
            apiCacheDao.insertApiCache(
                ApiCacheEntity(cacheKey = cacheKey, payloadJson = Json.encodeToString(response))
            )
        }
        response
    } catch (e: Exception) {
        if (e is com.example.attendease.data.api.ApiException || e is com.example.attendease.data.api.UnauthorizedException) throw e
        Log.w(logTag, "Network failed for $cacheKey, loading cache fallback", e)
        if (cached != null) {
            Json.decodeFromString<T>(cached.payloadJson)
        } else {
            throw e
        }
    }
}
