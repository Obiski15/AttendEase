package com.example.attendease.ui.navigation.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.screens.auth.LoginScreen


fun NavGraphBuilder.authGraph(
    navController: NavController
) {

    composable(Screen.Login.route) {
        LoginScreen(
            navController
        )
    }

}