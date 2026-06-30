package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.CourseRepository
import com.example.attendease.dto.response.CourseResponse
import com.example.attendease.state.CourseUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseViewModel(
    private val repository: CourseRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState = _uiState.asStateFlow()

    fun loadCourses(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }

            val cache = repository.getCachedCourses()
            if (cache != null && !refresh) {
                _uiState.update { it.copy(courses = cache) }
                        }
            if (cache == null || refresh) {
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                _uiState.update { it.copy(courses = repository.getCourses()) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createCourse(title: String, courseCode: String, creditUnits: Int, departmentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                repository.createCourse(title, courseCode, creditUnits, departmentId)
                _uiState.update { it.copy(saveSuccess = true) }
                loadCourses()
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

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(error = null) }
            try {
                _uiState.update { it.copy(currentCourse = repository.getCourse(courseId)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateCourse(courseId: String, title: String, courseCode: String, creditUnits: Int, departmentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                repository.updateCourse(courseId, title, courseCode, creditUnits, departmentId)
                _uiState.update { it.copy(saveSuccess = true) }
                loadCourses()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(error = null) }
            try {
                repository.deleteCourse(courseId)
                loadCourses()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
