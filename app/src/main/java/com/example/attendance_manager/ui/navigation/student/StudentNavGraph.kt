package com.example.attendance_manager.ui.navigation.student


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.attendance_manager.ui.navigation.Screen

fun NavGraphBuilder.studentGraph(
    navController: NavController
) {

    composable(Screen.StudentDashboard.route) {
//        StudentDashboardScreen(navController)
    }

    composable(Screen.ScanAttendance.route){
//        ScanAttendanceScreen(navController)
    }

    composable(Screen.AttendanceHistory.route) {
//        AttendanceHistoryScreen(navController)
    }

}