package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.StudentRepository
import com.example.attendease.dto.request.StudentCreateRequest
import com.example.attendease.dto.request.StudentUpdateRequest
import com.example.attendease.dto.response.DepartmentResponse
import com.example.attendease.dto.response.StudentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    private val _students = MutableStateFlow<List<StudentResponse>>(emptyList())
    val students = _students.asStateFlow()

    private val _departments = MutableStateFlow<List<DepartmentResponse>>(emptyList())
    val departments = _departments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    private val _currentStudent = MutableStateFlow<StudentResponse?>(null)
    val currentStudent = _currentStudent.asStateFlow()

    fun loadStudents() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _students.value = repository.getStudents()
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

    fun loadStudent(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _currentStudent.value = repository.getStudent(userId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCurrentStudent() {
        _currentStudent.value = null
    }

    fun createStudent(request: StudentCreateRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false
            try {
                repository.createStudent(request)
                _saveSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStudent(userId: String, request: StudentUpdateRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false
            try {
                repository.updateStudent(userId, request)
                _saveSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteStudent(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.deleteStudent(userId)
                loadStudents() // Reload list immediately
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
