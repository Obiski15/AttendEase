package com.example.attendease.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.attendease.ui.navigation.admin.adminGraph
import com.example.attendease.ui.navigation.auth.authGraph
import com.example.attendease.ui.navigation.lecturer.lecturerGraph
import com.example.attendease.ui.navigation.student.studentGraph
import com.example.attendease.ui.navigation.settings.settingsGraph

import com.example.attendease.data.session.SessionManager
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

    val startDestination = Screen.Splash.route

    NavHost(
        navController,
        startDestination = startDestination
    ) {

        composable(Screen.Splash.route) {
            com.example.attendease.ui.screens.common.SplashScreen(navController = navController)
        }

        authGraph(navController)

        adminGraph(navController)

        lecturerGraph(navController)

        studentGraph(navController)

        settingsGraph(navController)
    }
}