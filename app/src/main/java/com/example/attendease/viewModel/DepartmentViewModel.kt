package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.DepartmentRepository
import com.example.attendease.dto.response.DepartmentResponse
import com.example.attendease.state.DepartmentUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DepartmentViewModel(
    private val repository: DepartmentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DepartmentUiState())
    val uiState = _uiState.asStateFlow()

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
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createDepartment(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(saveSuccess = false) }
            try {
                repository.createDepartment(name)
                _uiState.update { it.copy(saveSuccess = true) }
                loadDepartments()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteDepartment(departmentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(error = null) }
            try {
                repository.deleteDepartment(departmentId)
                loadDepartments()
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
