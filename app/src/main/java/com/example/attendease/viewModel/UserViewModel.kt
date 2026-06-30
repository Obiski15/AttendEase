package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.UserRepository
import com.example.attendease.dto.request.UserCreateRequest
import com.example.attendease.dto.request.UserUpdateRequest
import com.example.attendease.dto.response.UserResponse
import com.example.attendease.state.UserUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            try {
                _uiState.update { it.copy(users = repository.getUsers()) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            try {
                _uiState.update { it.copy(currentUser = repository.getUser(userId)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createAdmin(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                val request = UserCreateRequest(
                    name = name,
                    email = email,
                    password = password,
                    role = "ADMIN"
                )
                repository.createUser(request)
                _uiState.update { it.copy(saveSuccess = true) }
                loadUsers()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateAdmin(userId: String, name: String, email: String, password: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                val request = UserUpdateRequest(
                    name = name,
                    email = email,
                    role = "ADMIN",
                    password = password.takeIf { !it.isNullOrBlank() }
                )
                repository.updateUser(userId, request)
                _uiState.update { it.copy(saveSuccess = true) }
                loadUsers()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.deleteUser(userId)
                loadUsers()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resetSaveState() {
        _uiState.update { it.copy(saveSuccess = false) }
        _uiState.update { it.copy(error = null) }
        _uiState.update { it.copy(currentUser = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
