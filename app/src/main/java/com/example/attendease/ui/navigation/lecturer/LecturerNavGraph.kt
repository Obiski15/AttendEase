package com.example.attendease.ui.navigation.lecturer

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.attendease.ui.navigation.Screen

import com.example.attendease.ui.screens.lecturer.LecturerActiveSessionScreen
import com.example.attendease.ui.screens.lecturer.LecturerDashboardScreen
import com.example.attendease.ui.screens.lecturer.LecturerSessionHistoryScreen
import com.example.attendease.viewModel.LecturerSessionViewModel
import org.koin.compose.koinInject

fun NavGraphBuilder.lecturerGraph(
    navController: NavController
) {

    composable(Screen.LecturerDashboard.route) {
        val sessionViewModel: LecturerSessionViewModel = koinInject()
        LecturerDashboardScreen(navController, sessionViewModel)
    }

    composable(Screen.StartSession.route) {
        val sessionViewModel: LecturerSessionViewModel = koinInject()
        LecturerActiveSessionScreen(navController, sessionViewModel)
    }

    composable(Screen.SessionHistory.route) {
        LecturerSessionHistoryScreen(navController)
    }
}
