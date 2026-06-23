package com.example.attendease.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.attendease.data.api.UnauthorizedException
import com.example.attendease.data.repository.AuthRepository
import com.example.attendease.dto.response.UserResponse
import com.example.attendease.ui.navigation.Screen
import org.koin.compose.koinInject

@Composable
fun AuthenticateUser(
    navController: NavController,
    authRepository: AuthRepository = koinInject(),
    onAuthenticated: (UserResponse) -> Unit
) {
    LaunchedEffect(Unit) {
        try {
            val user = authRepository.getMe()
            onAuthenticated(user)
        } catch (e: UnauthorizedException) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        } catch (e: Exception) {
            // Silence other errors
        }
    }
}
