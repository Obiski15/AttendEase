package com.example.attendance_manager.ui.navigation.student

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.attendance_manager.ui.navigation.Screen
import com.example.attendance_manager.ui.screens.student.ScanAttendanceScreen
import com.example.attendance_manager.ui.screens.student.StudentAttendanceHistoryScreen
import com.example.attendance_manager.ui.screens.student.StudentDashboardScreen

fun NavGraphBuilder.studentGraph(
    navController: NavController
) {

    composable(Screen.StudentDashboard.route) {
        StudentDashboardScreen(navController)
    }

    composable(Screen.ScanAttendance.route){
        ScanAttendanceScreen(navController)
    }

    composable(Screen.AttendanceHistory.route) {
        StudentAttendanceHistoryScreen(navController)
    }

}
