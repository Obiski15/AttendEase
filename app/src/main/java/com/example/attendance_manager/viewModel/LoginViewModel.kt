package com.example.attendance_manager.viewModel

import androidx.lifecycle.ViewModel
import com.example.attendance_manager.state.LoginUiState
import com.example.attendance_manager.state.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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

}