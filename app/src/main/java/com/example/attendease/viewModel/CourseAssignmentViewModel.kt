package com.example.attendease.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.data.repository.CourseAssignmentRepository
import com.example.attendease.state.CourseAssignmentUiState
import com.example.attendease.state.CourseAssignmentUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CourseAssignmentViewModel(
    private val repository: CourseAssignmentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CourseAssignmentUiState())
    val uiState = _uiState.asStateFlow()

    private val PAGE_SIZE = 20
    private var currentLecturerSearchQuery: String = ""

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val courses = repository.getCourses()
                val sessions = repository.getAcademicSessions()
                val rawAssignments = repository.getCourseAssignments()

                val assignments = rawAssignments.map { assignment ->
                    val course = assignment.course ?: courses.find { it.id == assignment.courseId }
                    val session = assignment.academicSession ?: sessions.find { it.id == assignment.academicSessionId }
                    CourseAssignmentUiModel(
                        id = assignment.id,
                        courseCode = course?.courseCode ?: "Unknown Code",
                        courseTitle = course?.title ?: "Unknown Title",
                        lecturerName = assignment.lecturer?.user?.name ?: "Unknown Lecturer",
                        lecturerRole = "Staff ID: ${assignment.lecturer?.staffId ?: "N/A"}",
                        lecturerId = assignment.lecturerId ?: "",
                        courseId = assignment.courseId ?: "",
                        academicSessionId = assignment.academicSessionId ?: "",
                        sessionName = session?.sessionName ?: "Unknown Session"
                    )
                }

                val assignedCourseIds = rawAssignments.mapNotNull { it.courseId }.toSet()
                val unassignedCourses = courses.filter { it.id !in assignedCourseIds }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        assignments = assignments,
                        unassignedCourses = unassignedCourses,
                        courses = courses,
                        academicSessions = sessions
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun searchLecturers(query: String) {
        currentLecturerSearchQuery = query
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDropdownLecturersLoading = true,
                    dropdownLecturersCurrentSkip = 0,
                    dropdownLecturersIsLastPage = false,
                    error = null
                )
            }
            try {
                val response = repository.searchLecturers(query = query, skip = 0, limit = PAGE_SIZE)
                _uiState.update {
                    it.copy(
                        isDropdownLecturersLoading = false,
                        dropdownLecturers = response.items,
                        dropdownLecturersCurrentSkip = PAGE_SIZE,
                        dropdownLecturersIsLastPage = response.items.size < PAGE_SIZE
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isDropdownLecturersLoading = false, error = e.message) }
            }
        }
    }

    fun loadMoreLecturers() {
        if (_uiState.value.isDropdownLecturersLoading || _uiState.value.dropdownLecturersIsLastPage) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDropdownLecturersLoading = true, error = null) }
            try {
                val currentSkip = _uiState.value.dropdownLecturersCurrentSkip
                val response = repository.searchLecturers(query = currentLecturerSearchQuery, skip = currentSkip, limit = PAGE_SIZE)

                _uiState.update { state ->
                    val newLecturers = state.dropdownLecturers + response.items
                    state.copy(
                        isDropdownLecturersLoading = false,
                        dropdownLecturers = newLecturers,
                        dropdownLecturersCurrentSkip = currentSkip + PAGE_SIZE,
                        dropdownLecturersIsLastPage = response.items.size < PAGE_SIZE
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isDropdownLecturersLoading = false, error = e.message) }
            }
        }
    }

    fun assignLecturer(courseId: String, lecturerId: String, academicSessionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, saveSuccess = false) }
            try {
                repository.createCourseAssignment(courseId, lecturerId, academicSessionId)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                loadData()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun reassignLecturer(oldAssignmentId: String, courseId: String, lecturerId: String, academicSessionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, saveSuccess = false) }
            try {
                repository.deleteCourseAssignment(oldAssignmentId)
                repository.createCourseAssignment(courseId, lecturerId, academicSessionId)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                loadData()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun removeAssignment(assignmentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, deleteSuccess = false) }
            try {
                repository.deleteCourseAssignment(assignmentId)
                _uiState.update { it.copy(deleteSuccess = true) }
                loadData()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun resetSaveState() {
        _uiState.update { it.copy(saveSuccess = false, deleteSuccess = false, error = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
