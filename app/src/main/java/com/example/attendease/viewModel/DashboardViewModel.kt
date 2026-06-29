package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.DashboardRepository
import com.example.attendease.dto.response.AdminDashboardResponse
import com.example.attendease.dto.response.LecturerDashboardResponse
import com.example.attendease.dto.response.StudentDashboardResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {
    private val _adminStats = MutableStateFlow<AdminDashboardResponse?>(null)
    val adminStats = _adminStats.asStateFlow()

    private val _lecturerStats = MutableStateFlow<LecturerDashboardResponse?>(null)
    val lecturerStats = _lecturerStats.asStateFlow()

    // 👇 NEW: holds the student dashboard data
    private val _studentStats = MutableStateFlow<StudentDashboardResponse?>(null)
    val studentStats = _studentStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadAdminStats(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _error.value = null
            val cache = repository.getCachedAdminDashboard()
            if (cache != null && !isRefresh) {
                _adminStats.value = cache
            }
            if (cache == null || isRefresh) {
                _error.value = null
                _isLoading.value = true
            }
            try {
                _adminStats.value = repository.getAdminDashboard()
            } catch (e: Exception) {
                if (_adminStats.value == null) {
                    _error.value = e.message
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLecturerDashboard(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _error.value = null
            val cache = repository.getCachedLecturerDashboard()
            if (cache != null && !isRefresh) {
                _lecturerStats.value = cache
            }
            if (cache == null || isRefresh) {
                _error.value = null
                _isLoading.value = true
            }
            try {
                _lecturerStats.value = repository.getLecturerDashboard()
            } catch (e: Exception) {
                if (_lecturerStats.value == null) {
                    _error.value = e.message
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // loads student dashboard on launch
    fun loadStudentDashboard(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _error.value = null

            // Check cache first so screen loads instantly
            val cache = repository.getCachedStudentDashboard()
            if (cache != null && !isRefresh) {
                _studentStats.value = cache
            }

            if (cache == null || isRefresh) {
                _error.value = null
                _isLoading.value = true
            }

            try {
                // Fetch fresh data from API
                _studentStats.value = repository.getStudentDashboard()
            } catch (e: Exception) {
                // Only show error if we have no cached data to show
                if (_studentStats.value == null) {
                    _error.value = e.message
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}