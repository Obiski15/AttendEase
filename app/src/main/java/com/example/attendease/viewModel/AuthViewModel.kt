package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.attendease.data.repository.AuthRepository
import com.example.attendease.enums.UserRole
import com.example.attendease.state.LoginUiState
import com.example.attendease.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val sessionManager: com.example.attendease.data.session.SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(LoginUiState())

    val uiState =
        _uiState.asStateFlow()

    fun updateEmail(email: String) {

        _uiState.value =
            _uiState.value.copy(
                email = email
            )

    }

    fun updatePassword(password: String) {

        _uiState.value =
            _uiState.value.copy(
                password = password
            )

    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun login(navController: NavController) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            try {

                val response =
                    repository.login(
                        uiState.value.email,
                        uiState.value.password
                    )

                if (sessionManager.isBiometricEnabled()) {
                    sessionManager.saveSecureCredentials(uiState.value.email, uiState.value.password)
                }

                val destination = when (response.user.role) {
                    UserRole.STUDENT -> Screen.StudentDashboard.route
                    UserRole.LECTURER -> Screen.LecturerDashboard.route
                    UserRole.ADMIN -> Screen.AdminDashboard.route
                }

                navController.navigate(destination) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }


                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )

            }
        }
    }

    fun loginWithSavedCredentials(navController: NavController) {
        val creds = sessionManager.getSecureCredentials()
        if (creds != null) {
            _uiState.value = _uiState.value.copy(email = creds.first, password = creds.second)
            login(navController)
        }
    }

    private val _changePasswordState = MutableStateFlow<Result<Unit>?>(null)
    val changePasswordState = _changePasswordState.asStateFlow()

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                repository.changePassword(oldPassword, newPassword)
                _changePasswordState.value = Result.success(Unit)
            } catch (e: Exception) {
                _changePasswordState.value = Result.failure(e)
            }
        }
    }

    fun resetChangePasswordState() {
        _changePasswordState.value = null
    }

    private val _updateProfileState = MutableStateFlow<Result<com.example.attendease.dto.response.UserResponse>?>(null)
    val updateProfileState = _updateProfileState.asStateFlow()

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            try {
                val updatedUser = repository.updateProfile(name, email)
                _updateProfileState.value = Result.success(updatedUser)
            } catch (e: Exception) {
                _updateProfileState.value = Result.failure(e)
            }
        }
    }

    fun resetUpdateProfileState() {
        _updateProfileState.value = null
    }
}