package com.example.attendease.ui.screens.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing

data class StudentSession(
    val day: String,
    val month: String,
    val code: String,
    val title: String,
    val time: String,
    val isPresent: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAttendanceHistoryScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    
    val dummySessions = listOf(
        StudentSession("24", "OCT", "CS301", "Data Structures & Algos", "09:00 AM - 10:30 AM", true),
        StudentSession("22", "OCT", "MATH204", "Linear Algebra", "11:00 AM - 12:30 PM", false),
        StudentSession("21", "OCT", "ENG102", "Technical Writing", "02:00 PM - 03:30 PM", true)
    )

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                title = "Attendance History",
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                rightIcon = Icons.Default.FilterList,
                showBadge = false
            )
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.STUDENT,
                currentRoute = Screen.AttendanceHistory.route,
                navController
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
                // Overall Rate Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(Spacing.lg)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "OVERRALL RATE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF006F62)
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "88%",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF000066)
                            )
                            Text(
                                "+2.4%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF006F62),
                                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        
                        LinearProgressIndicator(
                            progress = { 0.88f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF006F62),
                            trackColor = Color(0xFFEEEEEE)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    HistoryStatCard(
                        title = "TOTAL PRESENT",
                        value = "42",
                        icon = Icons.Default.CheckCircle,
                        iconColor = Color(0xFF006F62),
                        modifier = Modifier.weight(1f)
                    )
                    HistoryStatCard(
                        title = "TOTAL ABSENT",
                        value = "06",
                        icon = Icons.Default.Cancel,
                        iconColor = Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                // Segmented Filter
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFEEEEEE),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        val filters = listOf("ALL", "PRESENT", "ABSENT")
                        filters.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            Surface(
                                modifier = Modifier.weight(1f).height(36.dp).clickable { selectedFilter = filter },
                                color = if (isSelected) Color(0xFF000066) else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = filter,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Recent Sessions ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "(Last 30 Days)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            items(dummySessions) { session ->
                SessionItem(session)
            }
            
            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}

@Composable
fun HistoryStatCard(title: String, value: String, icon: ImageVector, iconColor: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor)
                }
            }
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(value, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}

@Composable
fun SessionItem(session: StudentSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Date Badge
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(session.month, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = Color.Gray)
                        Text(session.day, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.width(Spacing.md))
                
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFE8EAF6),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                session.code,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF000066)
                            )
                        }
                        Spacer(modifier = Modifier.width(Spacing.base))
                        Text(
                            session.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(session.time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Surface(
                color = if (session.isPresent) Color(0xFFE0F2F1) else Color(0xFFFFEBEE),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (session.isPresent) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (session.isPresent) Color(0xFF006F62) else Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (session.isPresent) "PRESENT" else "ABSENT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (session.isPresent) Color(0xFF006F62) else Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
}
