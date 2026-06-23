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

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

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
}