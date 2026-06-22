package com.example.attendance_manager.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendance_manager.ui.components.*
import com.example.attendance_manager.ui.navigation.Screen
import com.example.attendance_manager.ui.theme.Spacing

@Composable
fun AdminDashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                containerColor = MaterialTheme.colorScheme.background
            )
        },
        bottomBar = {
            AttendEaseBottomBar(
                currentRoute = Screen.AdminDashboard.route,
                items = listOf(
                    NavigationItem("Dashboard", Icons.Default.Dashboard, Screen.AdminDashboard.route),
                    NavigationItem("Students", Icons.Default.People, Screen.Students.route),
                    NavigationItem("Lecturers", Icons.Default.Badge, Screen.Lecturers.route),
                    NavigationItem("Settings", Icons.Default.Settings, "settings")
                ),
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                Spacer(modifier = Modifier.height(Spacing.base))
                Text(
                    text = "Hello, Administrator",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Here is the latest system overview.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Spacing.lg))
            }

            // Stats Grid 2x2
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    MiniStatCard(
                        title = "Total Students",
                        value = "1,240",
                        icon = Icons.Default.School,
                        modifier = Modifier.weight(1f)
                    )
                    MiniStatCard(
                        title = "Total Lecturers",
                        value = "85",
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    MiniStatCard(
                        title = "Total Courses",
                        value = "42",
                        icon = Icons.AutoMirrored.Filled.LibraryBooks,
                        modifier = Modifier.weight(1f)
                    )
                    MiniStatCard(
                        title = "Active Sessions",
                        value = "12",
                        icon = Icons.Default.Wifi,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
            }

            // Quick Actions Grid 2x2
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    GridActionCard(
                        title = "Manage Students",
                        icon = Icons.Default.GroupAdd,
                        onClick = { navController.navigate(Screen.Students.route) },
                        modifier = Modifier.weight(1f)
                    )
                    GridActionCard(
                        title = "Manage Lecturers",
                        icon = Icons.Default.Badge,
                        onClick = { navController.navigate(Screen.Lecturers.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    GridActionCard(
                        title = "Manage Courses",
                        icon = Icons.Default.AutoStories,
                        onClick = { navController.navigate(Screen.Courses.route) },
                        modifier = Modifier.weight(1f)
                    )
                    GridActionCard(
                        title = "Course Assignments",
                        icon = Icons.Default.AssignmentInd,
                        onClick = { navController.navigate(Screen.CourseAssignment.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}
