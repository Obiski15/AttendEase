package com.example.attendease.state

import com.example.attendease.dto.response.CourseResponse
import com.example.attendease.dto.response.LecturerResponse
import com.example.attendease.dto.response.AcademicSessionResponse

data class CourseAssignmentUiModel(
    val id: String,
    val courseCode: String,
    val courseTitle: String,
    val lecturerName: String,
    val lecturerRole: String,
    val lecturerId: String,
    val courseId: String,
    val academicSessionId: String,
    val sessionName: String
)

data class CourseAssignmentUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val assignments: List<CourseAssignmentUiModel> = emptyList(),
    val unassignedCourses: List<CourseResponse> = emptyList(),
    val courses: List<CourseResponse> = emptyList(),
    val dropdownLecturers: List<LecturerResponse> = emptyList(),
    val isDropdownLecturersLoading: Boolean = false,
    val dropdownLecturersCurrentSkip: Int = 0,
    val dropdownLecturersIsLastPage: Boolean = false,
    val academicSessions: List<AcademicSessionResponse> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false
)
