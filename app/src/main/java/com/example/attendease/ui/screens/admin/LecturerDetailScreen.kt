package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.attendease.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import com.example.attendease.viewModel.LecturerViewModel
import com.example.attendease.ui.components.DetailSkeleton
import androidx.compose.material3.MaterialTheme

@Composable
fun LecturerDetailScreen(
    navController: NavController,
    lecturerId: String,
    viewModel: LecturerViewModel = koinViewModel()
) {
    val currentLecturer by viewModel.currentLecturer.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(lecturerId) {
        viewModel.loadLecturer(lecturerId)
        viewModel.loadDepartments()
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                title = "Lecturer Details",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.ADMIN,
                currentRoute = "profile",
                navController
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            DetailSkeleton()
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(Spacing.md),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(Spacing.md),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            val lecturer = currentLecturer
            val deptName = departments.find { it.id == lecturer?.departmentId }?.name ?: "Unknown Department"

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item {
                    Spacer(modifier = Modifier.height(Spacing.base))
                    // Profile Section
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
                            modifier = Modifier
                                .size(32.dp)
                                .offset(x = (-4).dp, y = (-4).dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Text(
                        text = lecturer?.user?.name ?: "No Name",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lecturer?.staffId ?: "N/A",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    // Lecturer Information
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(Spacing.lg)) {
                            Text(
                                text = "Lecturer Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(Spacing.lg))

                            InfoRow(
                                icon = Icons.Default.Domain,
                                label = "DEPARTMENT",
                                value = deptName
                            )

                            Spacer(modifier = Modifier.height(Spacing.md))

                            InfoRow(
                                icon = Icons.Default.Email,
                                label = "EMAIL",
                                value = lecturer?.user?.email ?: "N/A"
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    // Action Buttons
                    OutlinedButton(
                        onClick = {
                            navController.navigate(Screen.EditLecturer.createRoute(lecturerId))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(Spacing.base))
                        Text("Edit Lecturer", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }
        }
    }
}
