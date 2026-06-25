package com.example.attendease.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import com.example.attendease.viewModel.LecturerSessionViewModel
import com.example.attendease.viewModel.DashboardViewModel
import com.example.attendease.dto.response.AttendanceSessionResponse
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.ui.components.ListSkeleton

data class LecturerSession(
    val code: String,
    val title: String,
    val date: String,
    val time: String,
    val checkedIn: Int,
    val total: Int,
    val isActive: Boolean = false,
    val originalResponse: AttendanceSessionResponse? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerSessionHistoryScreen(
    navController: NavController,
    sessionViewModel: LecturerSessionViewModel = koinViewModel(),
    dashboardViewModel: DashboardViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Active", "Completed")

    LaunchedEffect(Unit) {
        sessionViewModel.loadSessionHistory()
        dashboardViewModel.loadLecturerDashboard()
    }

    val historySessions by sessionViewModel.sessionsHistory.collectAsState()
    val lecturerStats by dashboardViewModel.lecturerStats.collectAsState()
    val isLoading by sessionViewModel.isLoading.collectAsState()
    val error by sessionViewModel.error.collectAsState()

    val sessions = remember(historySessions, lecturerStats, searchQuery, selectedFilter) {
        historySessions.mapNotNull { session ->
            val course = lecturerStats?.courses?.find { it.courseAssignmentId == session.courseAssignmentId }
            val courseCode = course?.courseCode ?: "Session"
            val courseTitle = course?.courseTitle ?: "Course Session"

            val dateString = session.sessionDate ?: "Unknown Date"
            val timeString = if (session.startTime != null && session.expiresAt != null) {
                try {
                    fun formatTime(isoStr: String): String {
                        var clean = isoStr.replace(" ", "T")
                        if (clean.contains(".")) clean = clean.substringBefore(".")
                        else if (clean.contains("+")) clean = clean.substringBefore("+")
                        else if (clean.contains("Z")) clean = clean.replace("Z", "")
                        
                        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                        val date = inputFormat.parse(clean)
                        
                        val outputFormat = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                        outputFormat.timeZone = java.util.TimeZone.getDefault()
                        return date?.let { outputFormat.format(it) } ?: ""
                    }
                    
                    val startStr = formatTime(session.startTime)
                    val endStr = formatTime(session.expiresAt)
                    if (startStr.isNotEmpty() && endStr.isNotEmpty()) "$startStr - $endStr" else "Open Session"
                } catch (e: Exception) {
                    "Open Session"
                }
            } else {
                "Open Session"
            }

            val isActive = session.status == "ACTIVE"

            val item = LecturerSession(
                code = courseCode,
                title = courseTitle,
                date = dateString,
                time = timeString,
                checkedIn = session.recordsCount ?: 0,
                total = session.totalStudents ?: 0,
                isActive = isActive,
                originalResponse = session
            )

            val matchesSearch = item.code.contains(searchQuery, ignoreCase = true) ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.date.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Active" -> item.isActive
                "Completed" -> !item.isActive
                else -> true
            }

            if (matchesSearch && matchesFilter) item else null
        }.sortedByDescending { it.originalResponse?.startTime ?: it.originalResponse?.createdAt ?: "" }
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(title = "Session History")
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.LECTURER,
                currentRoute = Screen.SessionHistory.route,
                navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                placeholder = { Text("Search by course code or date...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.md),
                contentPadding = PaddingValues(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.base)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF006F62),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (isLoading && sessions.isEmpty()) {
                ListSkeleton()
            } else if (sessions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(Spacing.base))
                        Text(
                            text = if (error != null) "Error: $error" else "No sessions found",
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(sessions) { sessionItem ->
                        LecturerSessionCard(
                            session = sessionItem,
                            onClick = if (sessionItem.isActive && sessionItem.originalResponse != null) {
                                {
                                    sessionViewModel.setActiveSession(sessionItem.originalResponse)
                                    navController.navigate(Screen.StartSession.route)
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LecturerSessionCard(session: LecturerSession, onClick: (() -> Unit)? = null) {
    val progress = if (session.total > 0) session.checkedIn.toFloat() / session.total else 0f
    val percentage = (progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE8EAF6),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = session.code,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000066)
                    )
                }
                
                Surface(
                    color = if (session.isActive) Color(0xFFE8EAF6) else Color(0xFFE0F2F1),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (session.isActive) Color(0xFF000066) else Color(0xFF006F62))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (session.isActive) "ACTIVE" else "COMPLETED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (session.isActive) Color(0xFF000066) else Color(0xFF006F62)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Text(
                text = session.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(Spacing.base))
                Text(session.date, style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(Spacing.xs))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(Spacing.base))
                Text(session.time, style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${session.checkedIn}/${session.total} students checked in",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000066)
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xs))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (session.isActive) Color(0xFF000066) else Color(0xFF006F62),
                trackColor = Color(0xFFEEEEEE)
            )
        }
    }
}
