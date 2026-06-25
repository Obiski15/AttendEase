package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.CourseRepository
import com.example.attendease.dto.response.CourseResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseViewModel(
    private val repository: CourseRepository
) : ViewModel() {
    private val _courses = MutableStateFlow<List<CourseResponse>>(emptyList())
    val courses = _courses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    fun loadCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _courses.value = repository.getCourses()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createCourse(title: String, courseCode: String, creditUnits: Int, departmentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false
            try {
                repository.createCourse(title, courseCode, creditUnits, departmentId)
                _saveSuccess.value = true
                loadCourses()
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

    private val _currentCourse = MutableStateFlow<CourseResponse?>(null)
    val currentCourse = _currentCourse.asStateFlow()

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _currentCourse.value = repository.getCourse(courseId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCourse(courseId: String, title: String, courseCode: String, creditUnits: Int, departmentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false
            try {
                repository.updateCourse(courseId, title, courseCode, creditUnits, departmentId)
                _saveSuccess.value = true
                loadCourses()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.deleteCourse(courseId)
                loadCourses()
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
