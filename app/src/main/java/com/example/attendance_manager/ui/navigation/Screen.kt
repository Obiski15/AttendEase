package com.example.attendance_manager.ui.navigation

sealed class Screen(val route: String) {

    // Auth
    data object Login : Screen("login")

    // Admin
    data object AdminDashboard : Screen("admin_dashboard")
    data object AddStudent: Screen(route = "add_student")
    data object Students : Screen("students")
    data object StudentDetail : Screen("student_detail/{studentId}") {
        fun createRoute(studentId: String): String {
            return "student_detail/$studentId"
        }
    }
    data object Lecturers : Screen("lecturers")
    data object AddLecturer: Screen("add_lecturer")
    data object AddCourse: Screen("add_course")
    data object Courses : Screen("courses")
    data object CourseAssignment : Screen("course_assignment")

    // Lecturer
    data object LecturerDashboard : Screen("lecturer_dashboard")
    data object StartSession : Screen("start_session")
    data object AttendanceList : Screen("attendance_list")

    // Student
    data object StudentDashboard : Screen("student_dashboard")
    data object ScanAttendance : Screen("scan_attendance")
    data object AttendanceHistory : Screen("attendance_history")
}