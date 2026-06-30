package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.LecturerRepository
import com.example.attendease.dto.request.LecturerCreateRequest
import com.example.attendease.dto.request.LecturerUpdateRequest
import com.example.attendease.dto.response.DepartmentResponse
import com.example.attendease.dto.response.LecturerResponse
import com.example.attendease.state.LecturerUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LecturerViewModel(private val repository: LecturerRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LecturerUiState())
    val uiState = _uiState.asStateFlow()

    private var currentSkip = 0
    private val PAGE_SIZE = 10
    private var isLastPage = false

    private var isFetching = false
    fun loadLecturers(refresh: Boolean = false) {
        if (refresh) {
            currentSkip = 0
            isLastPage = false
        }
        if (isLastPage || isFetching) return
        isFetching = true

        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }

            if (currentSkip == 0) {
                val cache = repository.getCachedLecturers()?.items
                if (cache != null && !refresh) {
                    _uiState.update { it.copy(lecturers = cache) }
                            }
            }
            if (currentSkip != 0 || _uiState.value.lecturers.isEmpty() || refresh) {
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                val response = repository.getLecturers(skip = currentSkip, limit = PAGE_SIZE)
                if (refresh || currentSkip == 0) {
                        _uiState.update { it.copy(lecturers = response.items) }
                } else {
                    _uiState.update { it.copy(lecturers = _uiState.value.lecturers + response.items) }
                }
                currentSkip += PAGE_SIZE
                isLastPage = response.items.isEmpty() || response.items.size < PAGE_SIZE
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                isFetching = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadMore() {
        loadLecturers(refresh = false)
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

    fun loadLecturer(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(error = null) }
            try {
                _uiState.update { it.copy(currentLecturer = repository.getLecturer(userId)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                isFetching = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearCurrentLecturer() {
        _uiState.update { it.copy(currentLecturer = null) }
    }

    fun createLecturer(request: LecturerCreateRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                repository.createLecturer(request)
                _uiState.update { it.copy(saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                isFetching = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateLecturer(userId: String, request: LecturerUpdateRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                repository.updateLecturer(userId, request)
                _uiState.update { it.copy(saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                isFetching = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteLecturer(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(error = null) }
            try {
                repository.deleteLecturer(userId)
                loadLecturers() // Reload list immediately
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                isFetching = false
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
