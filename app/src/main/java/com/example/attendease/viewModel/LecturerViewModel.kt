package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.LecturerRepository
import com.example.attendease.dto.request.LecturerCreateRequest
import com.example.attendease.dto.request.LecturerUpdateRequest
import com.example.attendease.dto.response.DepartmentResponse
import com.example.attendease.dto.response.LecturerResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LecturerViewModel(private val repository: LecturerRepository) : ViewModel() {
    private val _lecturers = MutableStateFlow<List<LecturerResponse>>(emptyList())
    val lecturers = _lecturers.asStateFlow()

    private val _departments = MutableStateFlow<List<DepartmentResponse>>(emptyList())
    val departments = _departments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    private val _currentLecturer = MutableStateFlow<LecturerResponse?>(null)
    val currentLecturer = _currentLecturer.asStateFlow()

    fun loadLecturers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _lecturers.value = repository.getLecturers()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadDepartments() {
        viewModelScope.launch {
            try {
                _departments.value = repository.getDepartments()
            } catch (e: Exception) {
                // Silently handle background metadata loading errors
            }
        }
    }

    fun loadLecturer(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _currentLecturer.value = repository.getLecturer(userId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCurrentLecturer() {
        _currentLecturer.value = null
    }

    fun createLecturer(request: LecturerCreateRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false
            try {
                repository.createLecturer(request)
                _saveSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLecturer(userId: String, request: LecturerUpdateRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false
            try {
                repository.updateLecturer(userId, request)
                _saveSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteLecturer(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.deleteLecturer(userId)
                loadLecturers() // Reload list immediately
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
