package com.example.attendance_manager.ui.screens.admin

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
import com.example.attendance_manager.ui.components.AttendEaseDropdown
import com.example.attendance_manager.ui.components.AttendEaseFormField
import com.example.attendance_manager.ui.components.AttendEaseTopAppBar
import com.example.attendance_manager.ui.components.SuccessModal
import com.example.attendance_manager.ui.theme.Spacing

@Composable
fun AddLecturerScreen(navController: NavController) {
    var staffId by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }

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
                    
                    // Profile Upload Section
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
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
                            color = Color(0xFF006F62),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Upload",
                                    tint = Color.White,
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

                item {
                    AttendEaseFormField(
                        label = "STAFF ID",
                        value = staffId,
                        onValueChange = { staffId = it },
                        placeholder = "L-2023-001",
                        trailingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Color.LightGray) }
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "FULL NAME",
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "Dr. Sarah Johnson"
                    )
                }

                item {
                    AttendEaseDropdown(
                        label = "DEPARTMENT",
                        value = department,
                        options = listOf("Computer Science", "Engineering", "Mathematics"),
                        onOptionSelected = { department = it }
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "EMAIL ADDRESS",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "sarah.johnson@university.edu",
                        trailingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.LightGray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "TEMPORARY PASSWORD",
                        value = password,
                        onValueChange = { password = it },
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
                        onClick = { showSuccessModal = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000066))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text("Save Lecturer", fontWeight = FontWeight.Bold)
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
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
