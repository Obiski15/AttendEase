package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.background
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendease.ui.components.AttendEaseFormField
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.SuccessModal
import com.example.attendease.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import com.example.attendease.viewModel.UserViewModel
import androidx.compose.material3.MaterialTheme

@Composable
fun EditAdminScreen(
    navController: NavController,
    userId: String,
    viewModel: UserViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var showSuccessModal by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUser.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    AttendEaseErrorDialog(errorMessage = error, onDismiss = { viewModel.clearError() })


    LaunchedEffect(Unit) {
        viewModel.loadUser(userId)
        viewModel.resetSaveState()
    }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            name = user.name ?: ""
            email = user.email ?: ""
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            showSuccessModal = true
            viewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                title = "Edit Admin",
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                showBadge = false
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    // Info Banner
                    Surface(
                        color = Color(0xFFF1F3F4),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(Spacing.md)) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column {
                                Text("Edit Administrator", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    "Change the administrator's profile name, email, or credentials.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.md))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                            .padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        AttendEaseFormField(
                            label = "Full Name",
                            value = name,
                            onValueChange = { name = it },
                            placeholder = "e.g. John Doe",
                            trailingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceVariant) }
                        )
                        AttendEaseFormField(
                            label = "Email Address",
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "e.g. admin@school.com",
                            trailingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceVariant) }
                        )
                        AttendEaseFormField(
                            label = "New Password (Optional)",
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "Leave blank to keep current password",
                            trailingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceVariant) }
                        )

                        
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank()) {
                                viewModel.updateAdmin(userId, name, email, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !isLoading && name.isNotBlank() && email.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.base))
                            Text("Save Administrator", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }

            if (showSuccessModal) {
                SuccessModal(
                    title = "Admin Saved",
                    message = "The administrator account has been successfully updated.",
                    onContinue = {
                        showSuccessModal = false
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
