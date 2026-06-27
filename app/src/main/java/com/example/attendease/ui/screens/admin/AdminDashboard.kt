package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.*
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.ui.components.AuthenticateUser
import org.koin.compose.koinInject
import com.example.attendease.viewModel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = koinInject()
) {
    var userName by remember { mutableStateOf("Administrator") }
    val context = androidx.compose.ui.platform.LocalContext.current

    AuthenticateUser(navController) { user ->
        userName = user.name ?: "Administrator"
    }

    val stats by viewModel.adminStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAdminStats()
    }

    LaunchedEffect(error) {
        error?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                containerColor = MaterialTheme.colorScheme.background
            )
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.ADMIN,
                currentRoute = Screen.AdminDashboard.route,
                navController
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.loadAdminStats(isRefresh = true) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {

                item {
                Spacer(modifier = Modifier.height(Spacing.base))
                Text(
                    text = "Hello, $userName",
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
                        value = stats?.totalStudents?.toString() ?: "-",
                        icon = Icons.Default.School,
                        modifier = Modifier.weight(1f)
                    )
                    MiniStatCard(
                        title = "Total Lecturers",
                        value = stats?.totalLecturers?.toString() ?: "-",
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    MiniStatCard(
                        title = "Total Courses",
                        value = stats?.totalCourses?.toString() ?: "-",
                        icon = Icons.AutoMirrored.Filled.LibraryBooks,
                        modifier = Modifier.weight(1f)
                    )
                    MiniStatCard(
                        title = "Active Sessions",
                        value = stats?.activeSessions?.toString() ?: "-",
                        icon = Icons.Default.Wifi,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (stats?.weeklyAttendanceTrend?.isNotEmpty() == true) {
                item {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = "Weekly Check-In Trend",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        WeeklyTrendChart(
                            trendData = stats!!.weeklyAttendanceTrend
                        )
                    }
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    GridActionCard(
                        title = "Academic Sessions",
                        icon = Icons.Default.DateRange,
                        onClick = { navController.navigate(Screen.AcademicSessions.route) },
                        modifier = Modifier.weight(1f)
                    )
                    GridActionCard(
                        title = "Manage Departments",
                        icon = Icons.Default.Domain,
                        onClick = { navController.navigate(Screen.Departments.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    GridActionCard(
                        title = "Manage Admins",
                        icon = Icons.Default.Security,
                        onClick = { navController.navigate(Screen.ManageAdmins.route) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}
}