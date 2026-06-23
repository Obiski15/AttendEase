package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendease.ui.components.AttendEaseDropdown
import com.example.attendease.ui.components.AttendEaseFormField
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.SuccessModal
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.viewModel.StudentViewModel
import com.example.attendease.dto.request.StudentUpdateRequest
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditStudentScreen(
    navController: NavController,
    studentId: String,
    viewModel: StudentViewModel = koinViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var matricNo by remember { mutableStateOf("") }
    var formStudentId by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var selectedDepartmentId by remember { mutableStateOf<String?>(null) }
    var level by remember { mutableStateOf("100 Level") }
    var showSuccessModal by remember { mutableStateOf(false) }

    val departments by viewModel.departments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val currentStudent by viewModel.currentStudent.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.resetSaveState()
        viewModel.loadDepartments()
        viewModel.loadStudent(studentId)
    }

    LaunchedEffect(currentStudent, departments) {
        currentStudent?.let { student ->
            fullName = student.user?.name ?: ""
            email = student.user?.email ?: ""
            matricNo = student.matricNumber ?: ""
            formStudentId = student.studentId ?: ""
            selectedDepartmentId = student.departmentId
            department = departments.find { it.id == student.departmentId }?.name ?: ""
            level = student.level ?: "100 Level"
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            showSuccessModal = true
        }
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                title = "Edit Student",
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

                // Form Fields
                item {
                    AttendEaseFormField(
                        label = "Full Name",
                        value = fullName,
                        onValueChange = { },
                        placeholder = "e.g. John Doe",
                        leadingIcon = Icons.Default.Person,
                        enabled = false
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "Email Address",
                        value = email,
                        onValueChange = { },
                        placeholder = "e.g. john.doe@university.edu",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = false
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "Matric Number",
                        value = matricNo,
                        onValueChange = { if (!isLoading) matricNo = it },
                        placeholder = "e.g. U2023/CS/1024",
                        leadingIcon = Icons.Default.Badge
                    )
                }

                item {
                    AttendEaseFormField(
                        label = "Student Id",
                        value = formStudentId,
                        onValueChange = { if (!isLoading) formStudentId = it },
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
                            options = departments.map { it.name },
                            onOptionSelected = { name ->
                                if (!isLoading) {
                                    department = name
                                    selectedDepartmentId = departments.find { it.name == name }?.id
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        AttendEaseDropdown(
                            label = "Level",
                            value = level,
                            options = listOf("100 Level", "200 Level", "300 Level", "400 Level", "500 Level"),
                            onOptionSelected = { if (!isLoading) level = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    
                    Button(
                        onClick = {
                            viewModel.updateStudent(
                                studentId,
                                StudentUpdateRequest(
                                    studentId = formStudentId,
                                    matricNumber = matricNo,
                                    departmentId = selectedDepartmentId,
                                    level = level
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        enabled = !isLoading && matricNo.isNotEmpty() && formStudentId.isNotEmpty() && selectedDepartmentId != null
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text("Update Student", fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }

            if (showSuccessModal) {
                SuccessModal(
                    title = "Student Updated",
                    message = "Student record has been successfully updated.",
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
