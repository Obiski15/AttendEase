package com.example.attendance_manager.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendance_manager.ui.components.AttendEaseDropdown
import com.example.attendance_manager.ui.components.AttendEaseFormField
import com.example.attendance_manager.ui.components.AttendEaseTopAppBar
import com.example.attendance_manager.ui.components.SuccessModal
import com.example.attendance_manager.ui.theme.Spacing

@Composable
fun AddStudentScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var matricNo by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Computer Science") }
    var level by remember { mutableStateOf("100 Level") }
    var showSuccessModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                title = "Add Student",
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                rightIcon = Icons.AutoMirrored.Filled.HelpOutline,
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
                            color = MaterialTheme.colorScheme.surfaceVariant
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
                            color = MaterialTheme.colorScheme.secondary,
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
                        text = "UPLOAD PORTRAIT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                // Form Fields
                item {
                    AttendEaseFormField(
                        label = "Full Name",
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "e.g. John Doe",
                        leadingIcon = Icons.Default.Person
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "Matric Number",
                        value = matricNo,
                        onValueChange = { matricNo = it },
                        placeholder = "e.g. U2023/CS/1024",
                        leadingIcon = Icons.Default.Badge
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "Student Id",
                        value = studentId,
                        onValueChange = { studentId = it },
                        placeholder = "e.g. M2301221",
                        leadingIcon = Icons.Default.Badge
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        AttendEaseDropdown(
                            label = "Department",
                            value = department,
                            options = listOf("Computer Science", "Engineering", "Mathematics"),
                            onOptionSelected = { department = it },
                            modifier = Modifier.weight(1f)
                        )
                        AttendEaseDropdown(
                            label = "Level",
                            value = level,
                            options = listOf("100 Level", "200 Level", "300 Level", "400 Level", "500 Level"),
                            onOptionSelected = { level = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    
                    Button(
                        onClick = { showSuccessModal = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text("Save Student", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }

            if (showSuccessModal) {
                SuccessModal(
                    title = "Student Added",
                    message = "New student record has been successfully created and synced.",
                    onContinue = {
                        showSuccessModal = false
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
