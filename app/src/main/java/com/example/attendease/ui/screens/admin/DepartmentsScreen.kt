package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.layout.*
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.components.AttendEaseConfirmDialog
import com.example.attendease.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import com.example.attendease.viewModel.DepartmentViewModel
import com.example.attendease.dto.response.DepartmentResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentsScreen(
    navController: NavController,
    viewModel: DepartmentViewModel = koinViewModel()
) {
    val departments by viewModel.departments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    AttendEaseErrorDialog(errorMessage = error, onDismiss = { viewModel.clearError() })

    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newDepartmentName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadDepartments()
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            showAddDialog = false
            newDepartmentName = ""
            viewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(title = "Manage Departments")
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.ADMIN,
                currentRoute = Screen.Departments.route,
                navController,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF006F62),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Department")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && departments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                

                if (departments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Text("No departments available.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = PaddingValues(top = Spacing.md, bottom = 80.dp)
                    ) {
                        items(departments, key = { it.id }) { dept ->
                            DepartmentCard(
                                department = dept,
                                onDelete = { viewModel.deleteDepartment(dept.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showAddDialog = false
                    viewModel.resetSaveState()
                },
                title = { Text("Add Department") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newDepartmentName,
                            onValueChange = { newDepartmentName = it },
                            label = { Text("Department Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (isLoading) {
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            if (newDepartmentName.isNotBlank()) {
                                viewModel.createDepartment(newDepartmentName)
                            }
                        },
                        enabled = !isLoading && newDepartmentName.isNotBlank()
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DepartmentCard(
    department: DepartmentResponse,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Domain,
                    contentDescription = null,
                    tint = Color(0xFF000066),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.md))
                Text(
                    text = department.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    AttendEaseConfirmDialog(
        show = showDeleteConfirm,
        title = "Delete Department",
        message = "Are you sure you want to delete '${department.name}'? This action cannot be undone.",
        onConfirm = {
            showDeleteConfirm = false
            onDelete()
        },
        onDismiss = { showDeleteConfirm = false }
    )
}
