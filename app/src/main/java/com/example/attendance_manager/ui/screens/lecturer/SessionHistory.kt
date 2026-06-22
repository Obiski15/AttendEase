package com.example.attendance_manager.ui.screens.lecturer

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendance_manager.ui.components.AttendEaseBottomBar
import com.example.attendance_manager.ui.components.AttendEaseTopAppBar
import com.example.attendance_manager.ui.components.NavigationItem
import com.example.attendance_manager.ui.navigation.Screen
import com.example.attendance_manager.ui.theme.Spacing

data class LecturerSession(
    val code: String,
    val title: String,
    val date: String,
    val time: String,
    val checkedIn: Int,
    val total: Int,
    val isActive: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerSessionHistoryScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Active", "Completed")

    val dummySessions = listOf(
        LecturerSession("CS301", "Data Structures", "Today, Oct 24", "10:00 AM - 11:30 AM", 42, 50, true),
        LecturerSession("MATH202", "Discrete Mathematics", "Oct 22, 2023", "02:00 PM - 04:00 PM", 48, 50),
        LecturerSession("ENG101", "Technical Writing", "Oct 20, 2023", "09:00 AM - 11:00 AM", 35, 50)
    )

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(title = "Session History")
        },
        bottomBar = {
            AttendEaseBottomBar(
                currentRoute = Screen.SessionHistory.route,
                items = listOf(
                    NavigationItem("Home", Icons.Default.Home, Screen.LecturerDashboard.route),
                    NavigationItem("History", Icons.Default.History, Screen.SessionHistory.route),
                    NavigationItem("Profile", Icons.Default.Person, "profile")
                ),
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
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

            // Filters
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
                            selectedContainerColor = Color(0xFF000066),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Session List
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(dummySessions) { session ->
                    LecturerSessionCard(session)
                }
            }
        }
    }
}

@Composable
fun LecturerSessionCard(session: LecturerSession) {
    val progress = session.checkedIn.toFloat() / session.total
    val percentage = (progress * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
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
