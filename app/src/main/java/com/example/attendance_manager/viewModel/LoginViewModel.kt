package com.example.attendance_manager.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance_manager.api.RetrofitClient
import com.example.attendance_manager.api.models.LoginRequest
import com.example.attendance_manager.state.LoginUiState
import com.example.attendance_manager.state.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

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

    fun updateUser(user: UserRole){
        _uiState.value = _uiState.value.copy(
            user = user
        )
    }

    fun login(onSuccess: (UserRole) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val request = LoginRequest(
                    email = _uiState.value.email,
                    password = _uiState.value.password,
                    role = _uiState.value.user.name
                )
                val response = RetrofitClient.attendanceApiService.login(request)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(loading = false)
                    onSuccess(_uiState.value.user)
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = "Login failed: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = "An error occurred: ${e.message}"
                )
            }
        }
    }

}