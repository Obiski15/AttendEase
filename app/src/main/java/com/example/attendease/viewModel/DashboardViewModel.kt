package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.DashboardRepository
import com.example.attendease.dto.response.AdminDashboardResponse
import com.example.attendease.dto.response.LecturerDashboardResponse
import com.example.attendease.dto.response.StudentDashboardResponse
import com.example.attendease.state.DashboardUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()


    // 👇 NEW: holds the student dashboard data

    fun loadAdminStats(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            val cache = repository.getCachedAdminDashboard()
            if (cache != null && !isRefresh) {
                _uiState.update { it.copy(adminStats = cache) }
                        }
            if (cache == null || isRefresh) {
                _uiState.update { it.copy(error = null) }
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                _uiState.update { it.copy(adminStats = repository.getAdminDashboard()) }
            } catch (e: Exception) {
                if (_uiState.value.adminStats == null) {
                    _uiState.update { it.copy(error = e.message) }
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadLecturerDashboard(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            val cache = repository.getCachedLecturerDashboard()
            if (cache != null && !isRefresh) {
                _uiState.update { it.copy(lecturerStats = cache) }
                        }
            if (cache == null || isRefresh) {
                _uiState.update { it.copy(error = null) }
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                _uiState.update { it.copy(lecturerStats = repository.getLecturerDashboard()) }
            } catch (e: Exception) {
                if (_uiState.value.lecturerStats == null) {
                    _uiState.update { it.copy(error = e.message) }
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // loads student dashboard on launch
    fun loadStudentDashboard(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }

            // Check cache first so screen loads instantly
            val cache = repository.getCachedStudentDashboard()
            if (cache != null && !isRefresh) {
                _uiState.update { it.copy(studentStats = cache) }
                        }

            if (cache == null || isRefresh) {
                _uiState.update { it.copy(error = null) }
                _uiState.update { it.copy(isLoading = true) }
            }

            try {
                // Fetch fresh data from API
                _uiState.update { it.copy(studentStats = repository.getStudentDashboard()) }
            } catch (e: Exception) {
                // Only show error if we have no cached data to show
                if (_uiState.value.studentStats == null) {
                    _uiState.update { it.copy(error = e.message) }
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}