package com.example.attendease.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminDashboardResponse(
    @SerialName("total_students") val totalStudents: Int,
    @SerialName("total_lecturers") val totalLecturers: Int,
    @SerialName("total_courses") val totalCourses: Int,
    @SerialName("active_sessions") val activeSessions: Int,
    @SerialName("weekly_attendance_trend") val weeklyAttendanceTrend: List<TrendPointResponse> = emptyList()
)

@Serializable
data class LecturerActiveSessionResponse(
    val id: String,
    @SerialName("course_code") val courseCode: String,
    @SerialName("course_title") val courseTitle: String,
    @SerialName("session_code") val sessionCode: String,
    @SerialName("expires_at") val expiresAt: String,
    @SerialName("geofencing_enabled") val geofencingEnabled: Boolean,
    @SerialName("radius_meters") val radiusMeters: Int? = null
)

@Serializable
data class PiePointResponse(
    val label: String,
    val count: Int
)

@Serializable
data class TrendPointResponse(
    @SerialName("date_str") val dateStr: String,
    val count: Int
)

@Serializable
data class LecturerCourseResponse(
    @SerialName("course_assignment_id") val courseAssignmentId: String,
    @SerialName("course_code") val courseCode: String,
    @SerialName("course_title") val courseTitle: String,
    @SerialName("credit_units") val creditUnits: Int
)

@Serializable
data class LecturerDashboardResponse(
    @SerialName("full_name") val fullName: String,
    @SerialName("assigned_courses") val assignedCourses: Int,
    @SerialName("total_sessions") val totalSessions: Int,
    @SerialName("active_sessions") val activeSessions: List<LecturerActiveSessionResponse>,
    val courses: List<LecturerCourseResponse>,
    @SerialName("course_distribution") val courseDistribution: List<PiePointResponse> = emptyList()
)

@Serializable
data class RecentAttendanceResponse(
    @SerialName("course_code") val courseCode: String,
    @SerialName("course_title") val courseTitle: String,
    @SerialName("session_date") val sessionDate: String,
    @SerialName("check_in_time") val checkInTime: String,
    val status: String
)

@Serializable
data class StudentDashboardResponse(
    @SerialName("full_name") val fullName: String,
    @SerialName("attendance_percentage") val attendancePercentage: Double,
    @SerialName("present_count") val presentCount: Int,
    @SerialName("total_count") val totalCount: Int,
    @SerialName("recent_attendance") val recentAttendance: List<RecentAttendanceResponse>
)
