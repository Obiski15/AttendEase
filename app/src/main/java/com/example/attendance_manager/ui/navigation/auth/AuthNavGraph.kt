package com.example.attendance_manager.ui.navigation.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.attendance_manager.ui.navigation.Screen
import com.example.attendance_manager.ui.screens.auth.LoginScreen


import com.example.attendance_manager.state.UserRole


fun NavGraphBuilder.authGraph(
    navController: NavController
) {

    composable(Screen.Login.route) {
        LoginScreen(
            onLoginSuccess = { role ->
                val destination = when (role) {
                    UserRole.STUDENT -> Screen.StudentDashboard.route
                    UserRole.LECTURER -> Screen.LecturerDashboard.route
                    UserRole.ADMIN -> Screen.AdminDashboard.route
                }
                navController.navigate(destination) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        )
    }

}