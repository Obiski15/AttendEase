package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.DashboardRepository
import com.example.attendease.dto.response.AdminDashboardResponse
import com.example.attendease.dto.response.LecturerDashboardResponse
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

    fun clearError() {
        _error.value = null
    }
}
