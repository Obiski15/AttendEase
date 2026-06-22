package com.example.attendance_manager.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendance_manager.ui.components.AttendEaseBottomBar
import com.example.attendance_manager.ui.components.AttendEaseTopAppBar
import com.example.attendance_manager.ui.components.NavigationItem
import com.example.attendance_manager.ui.navigation.Screen
import com.example.attendance_manager.ui.theme.Spacing

data class Assignment(
    val courseCode: String,
    val courseTitle: String,
    val lecturerName: String?,
    val lecturerRole: String?,
    val isUnassigned: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseAssignmentScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All Departments", "Computer Science", "Engineering", "Mathematics")
    var selectedCategory by remember { mutableStateOf("All Departments") }

    val assignments = listOf(
        Assignment("CSCI-402", "Distributed Systems", "Dr. Elena Rodriguez", "Senior Lecturer"),
        Assignment("MATH-201", "Linear Algebra II", "Prof. James Sterling", "Department Head"),
        Assignment("CSCI-105", "Introduction to CS", null, null, isUnassigned = true),
        Assignment("PHYS-304", "Quantum Mechanics", "Dr. Sarah Chen", "Associate Professor")
    )

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(title = "Course Assignments")
        },
        bottomBar = {
            AttendEaseBottomBar(
                currentRoute = Screen.CourseAssignment.route,
                items = listOf(
                    NavigationItem("Home", Icons.Default.Home, Screen.AdminDashboard.route),
                    NavigationItem("Scan", Icons.Default.QrCodeScanner, Screen.ScanAttendance.route),
                    NavigationItem("Assign", Icons.Default.AssignmentInd, Screen.CourseAssignment.route),
                    NavigationItem("Profile", Icons.Default.Person, "profile")
                ),
                onNavigate = { route -> navController.navigate(route) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = Color(0xFF006F62),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Assignment")
            }
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
                    .padding(horizontal = Spacing.md, vertical = Spacing.base),
                placeholder = { Text("Search courses or lecturers...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            // Categories
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.base),
                contentPadding = PaddingValues(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.base)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF000066),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Assignments List
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(assignments) { assignment ->
                    if (assignment.isUnassigned) {
                        UnassignedCourseCard(assignment)
                    } else {
                        AssignmentCard(assignment)
                    }
                }
            }
        }
    }
}

@Composable
fun AssignmentCard(assignment: Assignment) {
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
                Text(
                    text = assignment.courseCode,
                    color = Color(0xFF006F62),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
                }
            }
            
            Text(
                text = assignment.courseTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Lecturer Info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color.Gray
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column {
                        Text(assignment.lecturerName ?: "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(assignment.lecturerRole ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2EBF2), contentColor = Color(0xFF006F62)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Reassign", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Remove", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UnassignedCourseCard(assignment: Assignment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE), )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.xl).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFFEEEEEE)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(Spacing.md))
            Text("Course Unassigned", fontWeight = FontWeight.Bold)
            Text(assignment.courseCode, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(Spacing.md))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000066)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Quick Assign", modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}
