package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.AttendanceRepository
import com.example.attendease.dto.request.AttendanceCheckInRequest
import com.example.attendease.dto.response.AttendanceRecordResponse
import com.example.attendease.state.AttendanceUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val repository: AttendanceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState = _uiState.asStateFlow()


    fun checkIn(sessionCode: String, latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(checkInSuccess = null) }
            try {
                val request = AttendanceCheckInRequest(
                    sessionCode = sessionCode,
                    latitude = latitude,
                    longitude = longitude
                )
                val response = repository.checkIn(request)
                _uiState.update { it.copy(checkInSuccess = response) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Check-in failed. Please try again.") }
            } finally {
                isPaginating = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resetState() {
        _uiState.update { it.copy(checkInSuccess = null) }
        _uiState.update { it.copy(error = null) }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private var currentSkip = 0
    private val PAGE_SIZE = 10
    private var isLastPage = false

    private var isPaginating = false
    fun getMyAttendance(refresh: Boolean = false) {
        if (refresh) {
            currentSkip = 0
            isLastPage = false
        }
        if (isLastPage || isPaginating) return
        isPaginating = true

        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }

            if (currentSkip == 0) {
                val cache = repository.getCachedMyAttendance()?.items
                if (cache != null && !refresh) {
                    _uiState.update { it.copy(attendanceHistory = cache) }
                            }
            }
            if (currentSkip != 0 || _uiState.value.attendanceHistory.isEmpty() || refresh) {
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                val response = repository.getMyAttendance(skip = currentSkip, limit = PAGE_SIZE)
                if (refresh || currentSkip == 0) {
                        _uiState.update { it.copy(attendanceHistory = response.items) }
                } else {
                    _uiState.update { it.copy(attendanceHistory = _uiState.value.attendanceHistory + response.items) }
                }
                currentSkip += PAGE_SIZE
                isLastPage = response.items.isEmpty() || response.items.size < PAGE_SIZE
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to load attendance history.") }
            } finally {
                isPaginating = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadMoreHistory() {
        getMyAttendance(refresh = false)
    }
}
