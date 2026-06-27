package com.example.attendease.ui.screens.lecturer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AuthenticateUser
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.StatCard
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.viewModel.LecturerSessionViewModel
import com.example.attendease.viewModel.DashboardViewModel
import com.example.attendease.dto.response.AttendanceSessionResponse
import com.example.attendease.ui.components.CourseDistributionChart
import androidx.compose.material3.MaterialTheme

data class Course(
    val assignmentId: String,
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
    navController: NavController,
    sessionViewModel: LecturerSessionViewModel,
    dashboardViewModel: DashboardViewModel = org.koin.compose.koinInject()
) {
    var userName by remember { mutableStateOf("User") }

    AuthenticateUser(navController) { user ->
        userName = user.name ?: "User"
    }

    LaunchedEffect(Unit) {
        dashboardViewModel.loadLecturerDashboard()
    }

    val lecturerStats by dashboardViewModel.lecturerStats.collectAsState()
    val isLoading by dashboardViewModel.isLoading.collectAsState()
    val error by dashboardViewModel.error.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedAssignmentId by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("60") }
    var geofencingEnabled by remember { mutableStateOf(false) }
    var latitudeText by remember { mutableStateOf("") }
    var longitudeText by remember { mutableStateOf("") }
    var radiusText by remember { mutableStateOf("50") }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (fine || coarse) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            latitudeText = location.latitude.toString()
                            longitudeText = location.longitude.toString()
                        }
                    }
                } catch (e: SecurityException) {}
            }
        }
    )

    LaunchedEffect(geofencingEnabled) {
        if (geofencingEnabled && latitudeText.isEmpty() && longitudeText.isEmpty()) {
            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            if (hasFine || hasCoarse) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            latitudeText = location.latitude.toString()
                            longitudeText = location.longitude.toString()
                        }
                    }
                } catch (e: SecurityException) {}
            } else {
                permissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }
    }

    val activeSessionError by sessionViewModel.error.collectAsState()
    val activeSessionIsLoading by sessionViewModel.isLoading.collectAsState()

    val courses = lecturerStats?.courses?.map {
        Course(
            assignmentId = it.courseAssignmentId,
            code = it.courseCode,
            name = it.courseTitle,
            level = "${it.creditUnits} Credit Units",
            section = "Main"
        )
    } ?: emptyList()

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
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { dashboardViewModel.loadLecturerDashboard(isRefresh = true) },
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
                    if (error != null) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "Error loading stats: $error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                item {
                    StatCard(
                        title = "ASSIGNED COURSES",
                        value = "${lecturerStats?.assignedCourses ?: 0}",
                        icon = Icons.Default.Book,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                }

                item {
                    val activeSession = lecturerStats?.activeSessions?.firstOrNull()
                    val activeSessionCode = activeSession?.sessionCode ?: "None"
                    val isSessionActive = activeSessionCode != "None"

                    StatCard(
                        title = "ACTIVE SESSION",
                        value = activeSessionCode,
                        subtitle = if (isSessionActive) "Ongoing (Tap to View)" else "No live sessions",
                        icon = Icons.Default.Wifi,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = if (isSessionActive && activeSession != null) {
                            Modifier.clickable {
                                val sessionResponse = AttendanceSessionResponse(
                                    id = activeSession.id,
                                    courseAssignmentId = null,
                                    sessionDate = null,
                                    startTime = null,
                                    expiresAt = activeSession.expiresAt,
                                    sessionCode = activeSession.sessionCode,
                                    status = "ACTIVE",
                                    geofencingEnabled = activeSession.geofencingEnabled,
                                    latitude = null,
                                    longitude = null,
                                    radiusMeters = activeSession.radiusMeters
                                )
                                sessionViewModel.setActiveSession(sessionResponse)
                                navController.navigate(Screen.StartSession.route)
                            }
                        } else {
                            Modifier
                        }
                    )
                }

                item {
                    StatCard(
                        title = "TOTAL SESSIONS",
                        value = "${lecturerStats?.totalSessions ?: 0}",
                        icon = Icons.Default.CalendarToday,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                }

                if (lecturerStats?.courseDistribution?.isNotEmpty() == true) {
                    item {
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = "Sessions per Course",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            CourseDistributionChart(
                                distribution = lecturerStats!!.courseDistribution
                            )
                        }
                    }
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

                if (courses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No courses assigned to you in this session.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(courses) { course ->
                        CourseCard(course) {
                            selectedAssignmentId = course.assignmentId
                            showDialog = true
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; sessionViewModel.clearError() },
            title = { Text("Configure Attendance Session", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Duration (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs)
                    ) {
                        Checkbox(
                            checked = geofencingEnabled,
                            onCheckedChange = { geofencingEnabled = it }
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text("Enable Geofencing Boundary", style = MaterialTheme.typography.bodyMedium)
                    }

                    if (geofencingEnabled) {
                        OutlinedTextField(
                            value = latitudeText,
                            onValueChange = { latitudeText = it },
                            label = { Text("Center Latitude") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = longitudeText,
                            onValueChange = { longitudeText = it },
                            label = { Text("Center Longitude") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = radiusText,
                            onValueChange = { radiusText = it },
                            label = { Text("Allowed Radius (meters)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (activeSessionError != null) {
                        Text(
                            text = activeSessionError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = Spacing.xs)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val duration = durationText.toIntOrNull() ?: 60
                        val lat = latitudeText.toDoubleOrNull()
                        val lon = longitudeText.toDoubleOrNull()
                        val rad = radiusText.toIntOrNull()
                        sessionViewModel.startSession(
                            courseAssignmentId = selectedAssignmentId,
                            durationMinutes = duration,
                            geofencingEnabled = geofencingEnabled,
                            latitude = lat,
                            longitude = lon,
                            radiusMeters = rad
                        ) {
                            showDialog = false
                            navController.navigate(Screen.StartSession.route)
                        }
                    },
                    enabled = !activeSessionIsLoading
                ) {
                    if (activeSessionIsLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Start Session")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; sessionViewModel.clearError() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CourseCard(course: Course, onStartClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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