package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.AttendanceSessionRepository
import com.example.attendease.dto.request.AttendanceSessionCreateRequest
import com.example.attendease.dto.response.AttendanceRecordResponse
import com.example.attendease.dto.response.AttendanceSessionResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.attendease.data.api.AttendanceWebSocketClient

class LecturerSessionViewModel(
    private val sessionRepository: AttendanceSessionRepository,
    private val webSocketClient: AttendanceWebSocketClient
) : ViewModel() {

    private val _activeSession = MutableStateFlow<AttendanceSessionResponse?>(null)
    val activeSession = _activeSession.asStateFlow()

    private val _checkedInRecords = MutableStateFlow<List<AttendanceRecordResponse>>(emptyList())
    val checkedInRecords = _checkedInRecords.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _sessionsHistory = MutableStateFlow<List<AttendanceSessionResponse>>(emptyList())
    val sessionsHistory = _sessionsHistory.asStateFlow()

    private var pollingJob: Job? = null
    private var expirationJob: Job? = null

    fun startSession(
        courseAssignmentId: String,
        durationMinutes: Int,
        geofencingEnabled: Boolean,
        latitude: Double?,
        longitude: Double?,
        radiusMeters: Int?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                if (courseAssignmentId.startsWith("demo-")) {
                    throw IllegalArgumentException("Demo assignment fallback triggered")
                }
                val request = AttendanceSessionCreateRequest(
                    courseAssignmentId = courseAssignmentId,
                    durationMinutes = durationMinutes,
                    geofencingEnabled = geofencingEnabled,
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = radiusMeters
                )
                val session = sessionRepository.openSession(request)
                _activeSession.value = session
                onSuccess()
                startExpirationTimer(session)
                startPollingRecords(session.id)
            } catch (e: Exception) {
                if (courseAssignmentId.startsWith("demo-")) {
                    val mockSession = AttendanceSessionResponse(
                        id = java.util.UUID.randomUUID().toString(),
                        courseAssignmentId = courseAssignmentId,
                        sessionDate = "2026-06-24",
                        startTime = java.time.Instant.now().toString(),
                        expiresAt = java.time.Instant.now().plus(java.time.Duration.ofMinutes(durationMinutes.toLong())).toString(),
                        sessionCode = (100000..999999).random().toString(),
                        status = "ACTIVE",
                        geofencingEnabled = geofencingEnabled,
                        latitude = latitude ?: 6.5244,
                        longitude = longitude ?: 3.3792,
                        radiusMeters = radiusMeters ?: 50
                    )
                    _activeSession.value = mockSession
                    onSuccess()
                    startExpirationTimer(mockSession)
                } else {
                    _error.value = e.message ?: "Failed to start attendance session."
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setActiveSession(session: AttendanceSessionResponse) {
        _activeSession.value = session
        startExpirationTimer(session)
        startPollingRecords(session.id)
    }

    fun closeActiveSession(onSuccess: () -> Unit) {
        val sessionId = _activeSession.value?.id ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                sessionRepository.closeSession(sessionId)
                stopPollingRecords()
                expirationJob?.cancel()
                expirationJob = null
                _activeSession.value = null
                _checkedInRecords.value = emptyList()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to close attendance session."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchActiveSessionRecords() {
        val sessionId = _activeSession.value?.id ?: return
        viewModelScope.launch {
            try {
                val records = sessionRepository.getSessionRecords(sessionId)
                _checkedInRecords.value = records
            } catch (e: Exception) {
                // Fail silently during polling to avoid disruptive error UI
            }
        }
    }

    fun startPollingRecords(sessionId: String) {
        pollingJob?.cancel()
        
        // Initial fetch to ensure we have all records that occurred before we connected
        fetchActiveSessionRecords()
        
        pollingJob = viewModelScope.launch {
            try {
                webSocketClient.observeAttendance(sessionId).collect { record ->
                    // Prepend new record to the list
                    val currentList = _checkedInRecords.value
                    if (currentList.none { it.id == record.id }) {
                        _checkedInRecords.value = listOf(record) + currentList
                    }
                }
            } catch (e: Exception) {
                // If websocket fails, fallback to polling
                android.util.Log.e("WebSocketError", "WebSocket disconnected, falling back to polling: ${e.message}")
                while (true) {
                    try {
                        val records = sessionRepository.getSessionRecords(sessionId)
                        _checkedInRecords.value = records
                    } catch (e: Exception) {
                        // Ignore errors during polling
                    }
                    kotlinx.coroutines.delay(5000)
                }
            }
        }
    }

    private fun startExpirationTimer(session: AttendanceSessionResponse) {
        // Removed auto-close timer to prevent emulator clock skew from prematurely closing sessions.
        // Expiration is enforced securely by the backend is_open() check.
    }

    fun stopPollingRecords() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun loadSessionHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _sessionsHistory.value = sessionRepository.getAttendanceSessions()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load session history."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingRecords()
    }
}
