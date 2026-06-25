package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.background
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.border
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.layout.*
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.lazy.LazyColumn
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.material.icons.Icons
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.material.icons.filled.*
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.material3.*
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.runtime.*
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.Alignment
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.Modifier
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.draw.clip
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.graphics.Color
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.text.font.FontWeight
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.unit.dp
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.unit.sp
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.navigation.NavController
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.ui.components.AttendEaseFormField
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.ui.components.SuccessModal
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.ui.components.AttendEaseErrorDialog
import org.koin.androidx.compose.koinViewModel
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.viewModel.UserViewModel
import com.example.attendease.ui.components.AttendEaseErrorDialog

@Composable
fun AddAdminScreen(
    navController: NavController,
    viewModel: UserViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var showSuccessModal by remember { mutableStateOf(false) }

    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    AttendEaseErrorDialog(errorMessage = error, onDismiss = { viewModel.clearError() })


    LaunchedEffect(Unit) {
        viewModel.resetSaveState()
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
                title = "Add Admin",
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
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF006F62))
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column {
                                Text("New Administrator", fontWeight = FontWeight.Bold, color = Color(0xFF000066))
                                Text(
                                    "Registering a new system administrator allows them full database read and write access.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
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
                            trailingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray) }
                        )
                        AttendEaseFormField(
                            label = "Email Address",
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "e.g. admin@school.com",
                            trailingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.LightGray) }
                        )
                        AttendEaseFormField(
                            label = "Password",
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "Password string",
                            trailingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray) }
                        )

                        
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                                viewModel.createAdmin(name, email, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006F62)),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !isLoading && name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.base))
                            Text("Create Administrator", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }

            if (showSuccessModal) {
                SuccessModal(
                    title = "Admin Added",
                    message = "The administrator account has been successfully created.",
                    onContinue = {
                        showSuccessModal = false
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
