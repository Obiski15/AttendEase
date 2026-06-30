package com.example.attendease.state

import com.example.attendease.dto.response.UserResponse

data class UserUiState(
    val users: List<UserResponse> = emptyList(),
    val currentUser: UserResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)
