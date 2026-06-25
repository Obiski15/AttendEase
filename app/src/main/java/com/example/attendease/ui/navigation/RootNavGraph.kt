package com.example.attendease.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.attendease.ui.navigation.admin.adminGraph
import com.example.attendease.ui.navigation.auth.authGraph
import com.example.attendease.ui.navigation.lecturer.lecturerGraph
import com.example.attendease.ui.navigation.student.studentGraph
import com.example.attendease.ui.navigation.settings.settingsGraph

import com.example.attendease.data.session.SessionManager
import com.example.attendease.enums.UserRole
import org.koin.compose.koinInject

@Composable
fun RootNavGraph(
    sessionManager: SessionManager = koinInject()
) {
    val navController = rememberNavController()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        sessionManager.sessionExpiredFlow.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val startDestination = if (sessionManager.isLoggedIn()) {
        when (sessionManager.getUserRole()) {
            UserRole.STUDENT -> Screen.StudentDashboard.route
            UserRole.LECTURER -> Screen.LecturerDashboard.route
            UserRole.ADMIN -> Screen.AdminDashboard.route
            null -> Screen.Login.route
        }
    } else {
        Screen.Login.route
    }

    NavHost(
        navController,
        startDestination = startDestination
    ) {

        authGraph(navController)

        adminGraph(navController)

        lecturerGraph(navController)

        studentGraph(navController)

        settingsGraph(navController)
    }
}