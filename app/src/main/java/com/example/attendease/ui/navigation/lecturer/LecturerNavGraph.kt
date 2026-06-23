package com.example.attendease.ui.navigation.lecturer

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.attendease.ui.navigation.Screen

import com.example.attendease.ui.screens.lecturer.LecturerActiveSessionScreen
import com.example.attendease.ui.screens.lecturer.LecturerDashboardScreen
import com.example.attendease.ui.screens.lecturer.LecturerSessionHistoryScreen

fun NavGraphBuilder.lecturerGraph(
    navController: NavController
) {

    composable(Screen.LecturerDashboard.route) {
        LecturerDashboardScreen(navController)
    }

    composable(Screen.Courses.route) {
//        CoursesScreen(navController)
    }

    composable(Screen.StartSession.route) {
        LecturerActiveSessionScreen(navController)
    }

    composable(Screen.AttendanceList.route) {
//        CoursesScreen(navController)
    }

    composable(Screen.SessionHistory.route) {
        LecturerSessionHistoryScreen(navController)
    }
}
