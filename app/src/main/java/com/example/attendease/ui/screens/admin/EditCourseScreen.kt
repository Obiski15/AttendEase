package com.example.attendease.ui.screens.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendease.ui.components.AttendEaseDropdown
import com.example.attendease.ui.components.AttendEaseFormField
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.SuccessModal
import com.example.attendease.ui.components.AttendEaseConfirmDialog
import com.example.attendease.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import com.example.attendease.viewModel.CourseViewModel
import com.example.attendease.viewModel.DepartmentViewModel
import androidx.compose.material3.MaterialTheme

@Composable
fun EditCourseScreen(
    navController: NavController,
    courseId: String,
    courseViewModel: CourseViewModel = koinViewModel(),
    departmentViewModel: DepartmentViewModel = koinViewModel()
) {
    var courseCode by remember { mutableStateOf("") }
    var courseTitle by remember { mutableStateOf("") }
    var departmentId by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var creditUnits by remember { mutableIntStateOf(3) }
    
    var showSuccessModal by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val departments by departmentViewModel.departments.collectAsState()
    val currentCourse by courseViewModel.currentCourse.collectAsState()
    val saveSuccess by courseViewModel.saveSuccess.collectAsState()
    val isLoading by courseViewModel.isLoading.collectAsState()
    val error by courseViewModel.error.collectAsState()
    AttendEaseErrorDialog(errorMessage = error, onDismiss = { courseViewModel.clearError() })


    LaunchedEffect(Unit) {
        departmentViewModel.loadDepartments()
        courseViewModel.loadCourse(courseId)
        courseViewModel.resetSaveState()
    }

    LaunchedEffect(currentCourse) {
        currentCourse?.let { course ->
            courseCode = course.courseCode
            courseTitle = course.title
            departmentId = course.departmentId
            creditUnits = course.creditUnits
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            showSuccessModal = true
            courseViewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                title = "Edit Course",
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
                            Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column {
                                Text("Edit Course Details", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    "Modify the code, title, credit units, or department of the course.",
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
                            label = "Course Code",
                            value = courseCode,
                            onValueChange = { courseCode = it },
                            placeholder = "e.g. CSC401",
                            trailingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceVariant) }
                        )
                        AttendEaseFormField(
                            label = "Course Title",
                            value = courseTitle,
                            onValueChange = { courseTitle = it },
                            placeholder = "e.g. Artificial Intelligence",
                            trailingIcon = { Icon(Icons.Default.Title, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceVariant) }
                        )
                        
                        val deptOptions = departments.map { it.name }
                        val selectedDeptName = departments.find { it.id == departmentId }?.name ?: ""
                        
                        AttendEaseDropdown(
                            label = "Department",
                            value = selectedDeptName,
                            options = deptOptions,
                            onOptionSelected = { selectedName -> 
                                departmentId = departments.find { it.name == selectedName }?.id ?: ""
                            }
                        )

                        AttendEaseDropdown(
                            label = "Level",
                            value = level,
                            options = listOf("100 Level", "200 Level", "300 Level", "400 Level", "500 Level"),
                            onOptionSelected = { level = it }
                        )

                        Column {
                            Text("Credit Units", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedIconButton(onClick = { if (creditUnits > 0) creditUnits-- }, shape = RoundedCornerShape(8.dp)) {
                                    Icon(Icons.Default.Remove, contentDescription = null)
                                }
                                Surface(
                                    modifier = Modifier.weight(1f).height(40.dp).padding(horizontal = 8.dp),
                                    color = Color(0xFFF1F3F4),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(creditUnits.toString(), fontWeight = FontWeight.Bold)
                                    }
                                }
                                OutlinedIconButton(onClick = { creditUnits++ }, shape = RoundedCornerShape(8.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                }
                            }
                        }

                        
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Button(
                            onClick = {
                                courseViewModel.updateCourse(
                                    courseId = courseId,
                                    title = courseTitle,
                                    courseCode = courseCode,
                                    creditUnits = creditUnits,
                                    departmentId = departmentId
                                )
                            },
                            modifier = Modifier.weight(1.5f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(28.dp),
                            enabled = !isLoading && courseTitle.isNotBlank() && courseCode.isNotBlank() && departmentId.isNotBlank()
                        ) {
                            if (isLoading && !showDeleteConfirm) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(Spacing.base))
                                Text("Save Changes", fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(28.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                            enabled = !isLoading
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.base))
                            Text("Delete", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }

            if (showSuccessModal) {
                SuccessModal(
                    title = "Course Updated",
                    message = "The course details have been successfully saved.",
                    onContinue = {
                        showSuccessModal = false
                        navController.popBackStack()
                    }
                )
            }

            AttendEaseConfirmDialog(
                show = showDeleteConfirm,
                title = "Delete Course",
                message = "Are you sure you want to delete '$courseCode'? All associated session assignments and records will be affected.",
                onConfirm = {
                    showDeleteConfirm = false
                    courseViewModel.deleteCourse(courseId)
                    navController.popBackStack()
                },
                onDismiss = { showDeleteConfirm = false }
            )
        }
    }
}
