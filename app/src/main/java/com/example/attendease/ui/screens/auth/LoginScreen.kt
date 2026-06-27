package com.example.attendease.ui.screens.auth

import androidx.compose.foundation.background
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import com.example.attendease.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendease.ui.components.AttendEaseFormField
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.viewModel.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.example.attendease.data.session.SessionManager
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.navigation.Screen
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.example.attendease.utils.BiometricHelper
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    val sessionManager: SessionManager = koinInject()
    val isLoggedIn = remember { sessionManager.isLoggedIn() }

    val context = LocalContext.current
    var hasPromptedBiometric by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val destination = when (sessionManager.getUserRole()) {
                UserRole.STUDENT -> Screen.StudentDashboard.route
                UserRole.LECTURER -> Screen.LecturerDashboard.route
                UserRole.ADMIN -> Screen.AdminDashboard.route
                null -> null
            }
            if (destination != null) {
                navController.navigate(destination) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        } else if (!hasPromptedBiometric && sessionManager.isBiometricEnabled() && sessionManager.getSecureCredentials() != null) {
            hasPromptedBiometric = true
            val activity = context as? FragmentActivity
            if (activity != null && BiometricHelper.isBiometricAvailable(activity)) {
                val success = BiometricHelper.authenticate(
                    activity = activity,
                    title = "Fast Login",
                    subtitle = "Verify your identity to log in"
                )
                if (success) {
                    viewModel.loginWithSavedCredentials(navController)
                }
            }
        }
    }

    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.containerMargin),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = Spacing.xl, vertical = Spacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App Logo
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(24.dp))
                        )

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        Text(
                            text = "AttendEase",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = (-1).sp
                        )

                        Spacer(modifier = Modifier.height(Spacing.xs))

                        Text(
                            text = "Sign in to your account",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(Spacing.xl))

                        // Email Field
                        AttendEaseFormField(
                            label = "Email Address",
                            value = state.email,
                            onValueChange = { viewModel.updateEmail(it) },
                            placeholder = "e.g. user@university.edu",
                            leadingIcon = Icons.Default.Email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        Spacer(modifier = Modifier.height(Spacing.md))

                        // Password Field
                        AttendEaseFormField(
                            label = "Password",
                            value = state.password,
                            onValueChange = { viewModel.updatePassword(it) },
                            placeholder = "Enter your password",
                            leadingIcon = Icons.Default.Lock,
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        Spacer(modifier = Modifier.height(Spacing.md))

                        // Error Message
                        

                        // Login Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    viewModel.login(navController)
                                },
                                enabled = !state.isLoading,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Login",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(Spacing.base))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }

                        }

                        if (sessionManager.isBiometricEnabled() && sessionManager.getSecureCredentials() != null) {
                            Spacer(modifier = Modifier.height(Spacing.lg))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                FilledIconButton(
                                    onClick = {
                                        val activity = context as? FragmentActivity
                                        if (activity != null && BiometricHelper.isBiometricAvailable(activity)) {
                                            coroutineScope.launch {
                                                val success = BiometricHelper.authenticate(
                                                    activity = activity,
                                                    title = "Fast Login",
                                                    subtitle = "Verify your identity to log in"
                                                )
                                                if (success) {
                                                    viewModel.loginWithSavedCredentials(navController)
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(64.dp),
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Biometric Login",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        // Footer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Need help? ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Contact Admin",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    
        AttendEaseErrorDialog(
            errorMessage = state.error,
            onDismiss = { viewModel.clearError() }
        )
    }
}