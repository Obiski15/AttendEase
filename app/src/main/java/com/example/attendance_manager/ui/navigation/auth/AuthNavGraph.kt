package com.example.attendance_manager.ui.navigation.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.attendance_manager.ui.navigation.Screen
import com.example.attendance_manager.ui.screens.auth.LoginScreen


fun NavGraphBuilder.authGraph(
    navController: NavController
) {

    composable(Screen.Login.route) {
        LoginScreen()
    }

}