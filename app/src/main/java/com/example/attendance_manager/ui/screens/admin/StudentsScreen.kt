package com.example.attendance_manager.ui.screens.admin

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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendance_manager.ui.components.AttendEaseBottomBar
import com.example.attendance_manager.ui.components.AttendEaseTopAppBar
import com.example.attendance_manager.ui.components.NavigationItem
import com.example.attendance_manager.ui.navigation.Screen
import com.example.attendance_manager.ui.theme.Spacing

data class Student(
    val name: String,
    val matricNo: String,
    val department: String,
    val level: String,
    val imageUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val departments = listOf("ALL", "CS", "IT", "SE")
    val levels = listOf("100L", "200L", "300L", "400L", "500L")
    var selectedDept by remember { mutableStateOf("ALL") }

    val dummyStudents = listOf(
        Student("Alex Johnson", "CST/19/1024", "COMPUTER SCIENCE", "400 LEVEL"),
        Student("Sarah Miller", "ITE/20/2155", "INFORMATION TECH", "300 LEVEL"),
        Student("David Chen", "SWE/21/3091", "SOFTWARE ENG.", "200 LEVEL"),
        Student("Elena Rodriguez", "CST/18/0942", "COMPUTER SCIENCE", "500 LEVEL"),
        Student("Michael Smith", "ITE/22/4001", "INFORMATION TECH", "100 LEVEL")
    )

    Scaffold(
        topBar = {
            AttendEaseTopAppBar()
        },
        bottomBar = {
            AttendEaseBottomBar(
                currentRoute = Screen.Students.route,
                items = listOf(
                    NavigationItem("Dashboard", Icons.Default.Dashboard, Screen.AdminDashboard.route),
                    NavigationItem("Students", Icons.Default.People, Screen.Students.route),
                    NavigationItem("Lecturers", Icons.Default.Badge, Screen.Lecturers.route),
                    NavigationItem("Settings", Icons.Default.Settings, "settings")
                ),
                onNavigate = { route -> navController.navigate(route) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddStudent.route) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Student")
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
                placeholder = { Text("Search by name or matric number", color = Color.Gray) },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.base),
                contentPadding = PaddingValues(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.base)
            ) {
                items(departments) { dept ->
                    FilterChip(
                        selected = selectedDept == dept,
                        onClick = { selectedDept = dept },
                        label = { Text(dept) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                items(levels) { level ->
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text(level) }
                    )
                }
            }

            // Student List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(dummyStudents) { student ->
                    StudentCard(student) {
                        navController.navigate(Screen.StudentDetail.createRoute(student.matricNo))
                    }
                }
            }
        }
    }
}

@Composable
fun StudentCard(student: Student, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image Placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = student.matricNo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = student.department,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = student.level,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
