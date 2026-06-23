package com.example.attendease.ui.screens.admin


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import com.example.attendease.viewModel.StudentViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import com.example.attendease.ui.navigation.Screen

@Composable
fun StudentDetailScreen(
    navController: NavController,
    userId: String,
    viewModel: StudentViewModel = koinViewModel()
) {
    val currentStudent by viewModel.currentStudent.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadStudent(userId)
        viewModel.loadDepartments()
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                title = "Student Details",
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
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
            val student = currentStudent
            val deptName = departments.find { it.id == student?.departmentId }?.name ?: "Unknown Department"

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
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Text(
                        text = student?.user?.name ?: "No Name",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = student?.matricNumber ?: "N/A",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    // Attendance Rate Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(Spacing.lg)) {
                            Text(
                                text = "ATTENDANCE RATE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = "92%",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            LinearProgressIndicator(
                                progress = { 0.92f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                    }
                }

                item {
                    // Attended / Absences Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        DetailStatCard(
                            title = "Attended",
                            value = "45",
                            icon = Icons.Default.CheckCircle,
                            iconColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        DetailStatCard(
                            title = "Absences",
                            value = "4",
                            icon = Icons.Default.Cancel,
                            iconColor = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    // Student Information
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(Spacing.lg)) {
                            Text(
                                text = "Student Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(Spacing.lg))

                            InfoRow(
                                icon = Icons.Default.School,
                                label = "DEPARTMENT",
                                value = deptName
                            )

                            Spacer(modifier = Modifier.height(Spacing.md))

                            InfoRow(
                                icon = Icons.Default.Layers,
                                label = "LEVEL",
                                value = student?.level ?: "N/A"
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    // Action Buttons
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Spacer(modifier = Modifier.width(Spacing.base))
                        Text("View Attendance History", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(Spacing.base))

                    OutlinedButton(
                        onClick = {
                            navController.navigate(Screen.EditStudent.createRoute(userId))
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
                        Text("Edit Student", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }
        }
    }
}

@Composable
fun DetailStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
