package com.example.attendance_manager.state

enum class UserRole { STUDENT, LECTURER, ADMIN }

data class LoginUiState(
    val user: UserRole = UserRole.STUDENT,
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null
)