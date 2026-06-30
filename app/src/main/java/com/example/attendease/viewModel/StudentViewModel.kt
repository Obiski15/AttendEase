package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.StudentRepository
import com.example.attendease.dto.request.StudentCreateRequest
import com.example.attendease.dto.request.StudentUpdateRequest
import com.example.attendease.dto.response.DepartmentResponse
import com.example.attendease.dto.response.StudentResponse
import com.example.attendease.state.StudentUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState = _uiState.asStateFlow()

    private var currentSkip = 0
    private val PAGE_SIZE = 10
    private var isLastPage = false

    private var isPaginating = false
    fun loadStudents(refresh: Boolean = false) {
        if (refresh) {
            currentSkip = 0
            isLastPage = false
        }
        if (isLastPage || isPaginating) return
        isPaginating = true

        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }

            if (currentSkip == 0) {
                val cache = repository.getCachedStudents()?.items
                if (cache != null && !refresh) {
                    _uiState.update { it.copy(students = cache) }
                            }
            }
            if (currentSkip != 0 || _uiState.value.students.isEmpty() || refresh) {
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                val response = repository.getStudents(skip = currentSkip, limit = PAGE_SIZE)
                if (refresh || currentSkip == 0) {
                        _uiState.update { it.copy(students = response.items) }
                } else {
                    _uiState.update { it.copy(students = _uiState.value.students + response.items) }
                }
                currentSkip += PAGE_SIZE
                isLastPage = response.items.isEmpty() || response.items.size < PAGE_SIZE
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                isPaginating = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadMore() {
        loadStudents(refresh = false)
    }

    fun loadDepartments(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }

            val cache = repository.getCachedDepartments()
            if (cache != null && !refresh) {
                _uiState.update { it.copy(departments = cache) }
                        }
            if (cache == null || refresh) {
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                _uiState.update { it.copy(departments = repository.getDepartments()) }
            } catch (e: Exception) {
                // Silently handle background metadata loading errors
            }
        }
    }

    fun loadStudent(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            try {
                _uiState.update { it.copy(currentStudent = repository.getStudent(userId)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                isPaginating = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearCurrentStudent() {
        _uiState.update { it.copy(currentStudent = null) }
    }

    fun createStudent(request: StudentCreateRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                repository.createStudent(request)
                _uiState.update { it.copy(saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                isPaginating = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateStudent(userId: String, request: StudentUpdateRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                repository.updateStudent(userId, request)
                _uiState.update { it.copy(saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                isPaginating = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteStudent(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.deleteStudent(userId)
                loadStudents() // Reload list immediately
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                isPaginating = false
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
