package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendease.ui.components.AttendEaseDropdown
import com.example.attendease.ui.components.AttendEaseFormField
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.SuccessModal
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.viewModel.LecturerViewModel
import com.example.attendease.dto.request.LecturerCreateRequest
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.MaterialTheme

@Composable
fun AddLecturerScreen(
    navController: NavController,
    viewModel: LecturerViewModel = koinViewModel()
) {
    var staffId by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var selectedDepartmentId by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }

    val viewModelUiState by viewModel.uiState.collectAsState()

    val departments = viewModelUiState.departments
    val isLoading = viewModelUiState.isLoading
    val error = viewModelUiState.error
    val saveSuccess = viewModelUiState.saveSuccess

    LaunchedEffect(Unit) {
        viewModel.resetSaveState()
        viewModel.clearCurrentLecturer()
        viewModel.loadDepartments()
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            showSuccessModal = true
        }
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                title = "Add Lecturer",
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                showBadge = false,
                rightIcon = Icons.Default.Person
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    // Profile Upload Section (Mocked)
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.padding(24.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.surface)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Upload",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "Upload Profile Photo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                // Error Block
                error?.let { err ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(Spacing.md),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    AttendEaseFormField(
                        label = "STAFF ID",
                        value = staffId,
                        onValueChange = { if (!isLoading) staffId = it },
                        placeholder = "L-2023-001",
                        trailingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceVariant) }
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "FULL NAME",
                        value = fullName,
                        onValueChange = { if (!isLoading) fullName = it },
                        placeholder = "Dr. Sarah Johnson"
                    )
                }

                item {
                    AttendEaseDropdown(
                        label = "DEPARTMENT",
                        value = department,
                        options = departments.map { it.name },
                        onOptionSelected = { name ->
                            if (!isLoading) {
                                department = name
                                selectedDepartmentId = departments.find { it.name == name }?.id
                            }
                        }
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "EMAIL ADDRESS",
                        value = email,
                        onValueChange = { if (!isLoading) email = it },
                        placeholder = "sarah.johnson@university.edu",
                        trailingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceVariant) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "TEMPORARY PASSWORD",
                        value = password,
                        onValueChange = { if (!isLoading) password = it },
                        placeholder = "........",
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        helperText = "Lecturer will be prompted to change this on first login."
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    
                    Button(
                        onClick = {
                            viewModel.createLecturer(
                                LecturerCreateRequest(
                                    email = email,
                                    password = password,
                                    fullName = fullName,
                                    staffId = staffId,
                                    departmentId = selectedDepartmentId ?: ""
                                )
                            )
                        },
                        enabled = !isLoading && staffId.isNotEmpty() && selectedDepartmentId != null && fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(
                                text = "Save Lecturer",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }

            if (showSuccessModal) {
                SuccessModal(
                    title = "Lecturer Added",
                    message = "New lecturer record has been successfully created and synced.",
                    onContinue = {
                        showSuccessModal = false
                        viewModel.resetSaveState()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
