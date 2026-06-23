package com.example.attendease.ui.navigation.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.screens.common.SettingsScreen

fun NavGraphBuilder.settingsGraph(
    navController: NavController
) {
    composable(
        route = Screen.Settings.route,
        arguments = listOf(
            navArgument("role") {
                type = NavType.StringType
                defaultValue = "ADMIN"
            },
            navArgument("name") {
                type = NavType.StringType
                defaultValue = "Admin User"
            },
            navArgument("email") {
                type = NavType.StringType
                defaultValue = "admin@university.edu"
            }
        )
    ) { backStackEntry ->
        val roleStr = backStackEntry.arguments?.getString("role") ?: "ADMIN"
        val name = backStackEntry.arguments?.getString("name") ?: "Admin User"
        val email = backStackEntry.arguments?.getString("email") ?: "admin@university.edu"
        
        val userRole = try {
            UserRole.valueOf(roleStr)
        } catch (e: IllegalArgumentException) {
            UserRole.ADMIN
        }

        SettingsScreen(
            navController = navController,
            userRole = userRole,
            userName = name,
            userEmail = email
        )
    }
}
