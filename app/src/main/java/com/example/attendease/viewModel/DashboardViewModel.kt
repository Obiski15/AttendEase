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

    fun loadAdminStats() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _adminStats.value = repository.getAdminDashboard()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLecturerDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _lecturerStats.value = repository.getLecturerDashboard()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
