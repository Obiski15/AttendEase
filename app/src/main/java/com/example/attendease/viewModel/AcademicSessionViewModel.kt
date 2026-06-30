package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.AcademicSessionRepository
import com.example.attendease.dto.response.AcademicSessionResponse
import com.example.attendease.state.AcademicSessionUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AcademicSessionViewModel(
    private val repository: AcademicSessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AcademicSessionUiState())
    val uiState = _uiState.asStateFlow()

    fun loadSessions(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }

            val cache = repository.getCachedAcademicSessions()
            if (cache != null && !refresh) {
                _uiState.update { it.copy(sessions = cache) }
                        }
            if (cache == null || refresh) {
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                _uiState.update { it.copy(sessions = repository.getAcademicSessions()) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createSession(sessionName: String, semester: String, isActive: Boolean, startDate: String, endDate: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                repository.createAcademicSession(sessionName, semester, isActive, startDate, endDate)
                _uiState.update { it.copy(saveSuccess = true) }
                loadSessions()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun activateSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.activateAcademicSession(sessionId)
                loadSessions()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateSession(sessionId: String, sessionName: String, semester: String, isActive: Boolean, startDate: String, endDate: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                repository.updateAcademicSession(sessionId, sessionName, semester, isActive, startDate, endDate)
                _uiState.update { it.copy(saveSuccess = true) }
                loadSessions()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.deleteAcademicSession(sessionId)
                loadSessions()
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
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
