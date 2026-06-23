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

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val courses = repository.getCourses()
                val lecturers = repository.getLecturers()
                val sessions = repository.getAcademicSessions()
                val rawAssignments = repository.getCourseAssignments()

                val assignments = rawAssignments.map { assignment ->
                    val course = courses.find { it.id == assignment.courseId }
                    val lecturer = lecturers.find { it.userId == assignment.lecturerId }
                    val session = sessions.find { it.id == assignment.academicSessionId }
                    CourseAssignmentUiModel(
                        id = assignment.id,
                        courseCode = course?.courseCode ?: "Unknown Code",
                        courseTitle = course?.title ?: "Unknown Title",
                        lecturerName = lecturer?.user?.name ?: "Unknown Lecturer",
                        lecturerRole = "Staff ID: ${lecturer?.staffId ?: "N/A"}",
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
                        lecturers = lecturers,
                        academicSessions = sessions
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
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
}
