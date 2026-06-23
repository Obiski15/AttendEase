package com.example.attendease.ui.navigation.student

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.screens.student.ScanAttendanceScreen
import com.example.attendease.ui.screens.student.StudentAttendanceHistoryScreen
import com.example.attendease.ui.screens.student.StudentDashboardScreen

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
