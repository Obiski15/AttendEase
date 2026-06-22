package com.example.attendance_manager.ui.navigation.lecturer

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.attendance_manager.ui.navigation.Screen

fun NavGraphBuilder.lecturerGraph(
    navController: NavController
) {

    composable(Screen.LecturerDashboard.route) {
//        LecturerDashboardScreen(navController)
    }

    composable(Screen.Courses.route) {
//        CoursesScreen(navController)
    }

    composable(Screen.StartSession.route) {
//        CoursesScreen(navController)
    }

    composable(Screen.AttendanceList.route) {
//        CoursesScreen(navController)
    }

}