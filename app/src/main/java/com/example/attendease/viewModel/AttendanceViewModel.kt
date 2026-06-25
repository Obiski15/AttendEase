package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.AttendanceRepository
import com.example.attendease.dto.request.AttendanceCheckInRequest
import com.example.attendease.dto.response.AttendanceRecordResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val repository: AttendanceRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _checkInSuccess = MutableStateFlow<AttendanceRecordResponse?>(null)
    val checkInSuccess = _checkInSuccess.asStateFlow()

    fun checkIn(sessionCode: String, latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _checkInSuccess.value = null
            try {
                val request = AttendanceCheckInRequest(
                    sessionCode = sessionCode,
                    latitude = latitude,
                    longitude = longitude
                )
                val response = repository.checkIn(request)
                _checkInSuccess.value = response
            } catch (e: Exception) {
                _error.value = e.message ?: "Check-in failed. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _checkInSuccess.value = null
        _error.value = null
        _isLoading.value = false
    }

    fun clearError() {
        _error.value = null
    }
}
