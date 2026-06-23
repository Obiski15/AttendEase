package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.AcademicSessionRepository
import com.example.attendease.dto.response.AcademicSessionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AcademicSessionViewModel(
    private val repository: AcademicSessionRepository
) : ViewModel() {
    private val _sessions = MutableStateFlow<List<AcademicSessionResponse>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    fun loadSessions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _sessions.value = repository.getAcademicSessions()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createSession(sessionName: String, semester: String, isActive: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false
            try {
                repository.createAcademicSession(sessionName, semester, isActive)
                _saveSuccess.value = true
                loadSessions()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun activateSession(sessionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.activateAcademicSession(sessionId)
                loadSessions()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.deleteAcademicSession(sessionId)
                loadSessions()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSaveState() {
        _saveSuccess.value = false
        _error.value = null
    }
}
