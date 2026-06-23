package com.example.attendease.ui.screens.lecturer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AuthenticateUser
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.StatCard
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing

data class Course(
    val code: String,
    val name: String,
    val level: String,
    val section: String,
    val isActive: Boolean = false,
    val canStart: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerDashboardScreen(
    navController: NavController
) {
    var userName by remember { mutableStateOf("User") }

    AuthenticateUser(navController) { user ->
        userName = user.name ?: "User"
    }

    val dummyCourses = listOf(
        Course("CS301", "Data Structures & Algorithms", "B.Tech Year 2", "Section A", isActive = true),
        Course("SE402", "Software Architecture", "B.Tech Year 3", "Section B"),
        Course("IT205", "Database Management", "B.Tech Year 2", "Section C", canStart = false),
        Course("CS510", "Machine Learning Basics", "M.Tech Year 1", "Section A")
    )

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                containerColor = MaterialTheme.colorScheme.background
            )
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.LECTURER,
                currentRoute = Screen.LecturerDashboard.route,
                navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
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
                    text = "Welcome back, $userName",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Here is an overview of your current academic sessions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Spacing.lg))
            }

            item {
                StatCard(
                    title = "ASSIGNED COURSES",
                    value = "4",
                    icon = Icons.Default.Book,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            }

            item {
                StatCard(
                    title = "ACTIVE SESSION",
                    value = "CS301",
                    subtitle = "Ongoing",
                    icon = Icons.Default.Wifi,
                    containerColor = MaterialTheme.colorScheme.primaryFixed,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }

            item {
                StatCard(
                    title = "TOTAL SESSIONS",
                    value = "48",
                    icon = Icons.Default.CalendarToday,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = "Assigned Courses",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
            }

            items(dummyCourses) { course ->
                CourseCard(course) {
                    navController.navigate(Screen.StartSession.route)
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}

@Composable
fun CourseCard(course: Course, onStartClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryFixed,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = course.code,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${course.level} • ${course.section}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            
            if (course.canStart) {
                Button(
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("Start Session", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("View History", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
