package com.example.attendease.data.session

import android.content.Context
import android.content.SharedPreferences
import com.example.attendease.enums.UserRole

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "attendease_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    }

    fun saveSession(
        accessToken: String,
        refreshToken: String,
        role: UserRole,
        name: String?,
        email: String?
    ) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ROLE, role.name)
            putString(KEY_USER_NAME, name ?: "User")
            putString(KEY_USER_EMAIL, email ?: "")
            apply()
        }
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    fun getUserRole(): UserRole? {
        val roleStr = prefs.getString(KEY_USER_ROLE, null) ?: return null
        return try { UserRole.valueOf(roleStr) } catch (e: Exception) { null }
    }
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, "User")
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, "")

    fun isLoggedIn(): Boolean = getAccessToken() != null

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    val sessionExpiredFlow = kotlinx.coroutines.flow.MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    suspend fun clearSessionAndNotify() {
        clearSession()
        sessionExpiredFlow.emit(Unit)
    }
}
