package com.example.attendease.data.session

import android.content.Context
import android.content.SharedPreferences
import com.example.attendease.enums.UserRole

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        SECURE_PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val PREFS_NAME = "attendease_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_THEME_PREFERENCE = "theme_preference"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        
        private const val SECURE_PREFS_NAME = "attendease_secure_prefs"
        private const val KEY_SECURE_EMAIL = "secure_email"
        private const val KEY_SECURE_PASSWORD = "secure_password"
    }

    val themePreferenceFlow = kotlinx.coroutines.flow.MutableStateFlow(getThemePreference())

    fun saveThemePreference(theme: String) {
        prefs.edit().putString(KEY_THEME_PREFERENCE, theme).apply()
        themePreferenceFlow.value = theme
    }

    fun getThemePreference(): String = prefs.getString(KEY_THEME_PREFERENCE, "SYSTEM") ?: "SYSTEM"

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

    fun isBiometricEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
        if (!enabled) {
            clearSecureCredentials()
        }
    }

    fun saveSecureCredentials(email: String, pass: String) {
        securePrefs.edit().apply {
            putString(KEY_SECURE_EMAIL, email)
            putString(KEY_SECURE_PASSWORD, pass)
            apply()
        }
    }

    fun getSecureCredentials(): Pair<String, String>? {
        val email = securePrefs.getString(KEY_SECURE_EMAIL, null)
        val pass = securePrefs.getString(KEY_SECURE_PASSWORD, null)
        if (email != null && pass != null) {
            return Pair(email, pass)
        }
        return null
    }

    fun clearSecureCredentials() {
        securePrefs.edit().clear().apply()
    }

    fun clearSession() {
        val biometric = isBiometricEnabled()
        val theme = getThemePreference()
        prefs.edit().clear().apply()
        setBiometricEnabled(biometric)
        saveThemePreference(theme)
    }

    val sessionExpiredFlow = kotlinx.coroutines.flow.MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    suspend fun clearSessionAndNotify() {
        clearSession()
        sessionExpiredFlow.emit(Unit)
    }
}
