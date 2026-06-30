package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.AttendanceSessionRepository
import com.example.attendease.dto.request.AttendanceSessionCreateRequest
import com.example.attendease.dto.response.AttendanceRecordResponse
import com.example.attendease.dto.response.AttendanceSessionResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.example.attendease.state.LecturerSessionUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.attendease.data.api.AttendanceWebSocketClient

class LecturerSessionViewModel(
    private val sessionRepository: AttendanceSessionRepository,
    private val webSocketClient: AttendanceWebSocketClient
) : ViewModel() {
    private val _uiState = MutableStateFlow(LecturerSessionUiState())
    val uiState = _uiState.asStateFlow()


    fun setActiveCourseTitle(title: String) {
        _uiState.update { it.copy(activeCourseTitle = title) }
    }

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
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
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
                _uiState.update { it.copy(activeSession = session) }
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
                    _uiState.update { it.copy(activeSession = mockSession) }
                    onSuccess()
                    startExpirationTimer(mockSession)
                } else {
                    _uiState.update { it.copy(error = e.message ?: "Failed to start attendance session.") }
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun setActiveSession(session: AttendanceSessionResponse) {
        _uiState.update { it.copy(activeSession = session) }
        startExpirationTimer(session)
        startPollingRecords(session.id)
    }

    fun closeActiveSession(onSuccess: () -> Unit) {
        val sessionId = _uiState.value.activeSession?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            try {
                sessionRepository.closeSession(sessionId)
                stopPollingRecords()
                expirationJob?.cancel()
                expirationJob = null
                _uiState.update { it.copy(activeSession = null) }
                _uiState.update { it.copy(locallyClosedSessionIds = _uiState.value.locallyClosedSessionIds + sessionId) }
                _uiState.update { it.copy(checkedInRecords = emptyList()) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to close attendance session.") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun fetchActiveSessionRecords() {
        val sessionId = _uiState.value.activeSession?.id ?: return
        viewModelScope.launch {
            try {
                val records = sessionRepository.getSessionRecords(sessionId)
                _uiState.update { it.copy(checkedInRecords = records) }
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
                    val currentList = _uiState.value.checkedInRecords
                    if (currentList.none { it.id == record.id }) {
                        _uiState.update { it.copy(checkedInRecords = listOf(record) + currentList) }
                    }
                }
            } catch (e: Exception) {
                // If websocket fails, fallback to polling
                android.util.Log.e("WebSocketError", "WebSocket disconnected, falling back to polling: ${e.message}")
                while (true) {
                    try {
                        val records = sessionRepository.getSessionRecords(sessionId)
                        _uiState.update { it.copy(checkedInRecords = records) }
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

    private var currentSkip = 0
    private val PAGE_SIZE = 10
    private var isLastPage = false
    private var isPaginating = false

    fun loadSessionHistory(refresh: Boolean = false) {
        if (refresh) {
            currentSkip = 0
            isLastPage = false
        }
        if (isLastPage || isPaginating) return
        isPaginating = true

        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }

            if (currentSkip == 0) {
                val cache = sessionRepository.getCachedAttendanceSessions()?.items
                if (cache != null && !refresh) {
                    _uiState.update { it.copy(sessionsHistory = cache) }
                            }
            }
            if (currentSkip != 0 || _uiState.value.sessionsHistory.isEmpty() || refresh) {
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                val response = sessionRepository.getAttendanceSessions(skip = currentSkip, limit = PAGE_SIZE)
                if (refresh || currentSkip == 0) {
                        _uiState.update { it.copy(sessionsHistory = response.items) }
                } else {
                    _uiState.update { it.copy(sessionsHistory = _uiState.value.sessionsHistory + response.items) }
                }
                currentSkip += PAGE_SIZE
                isLastPage = response.items.isEmpty() || response.items.size < PAGE_SIZE
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to load session history.") }
            } finally {
                isPaginating = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadMoreHistory() {
        loadSessionHistory(refresh = false)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        stopPollingRecords()
    }
}
