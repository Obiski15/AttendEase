package com.example.attendance_manager.ui.screens.admin

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

data class CourseModel(
    val code: String,
    val title: String,
    val department: String,
    val level: String,
    val lecturer: String?,
    val attendance: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All Courses", "Computer Science", "Engineering", "Mathematics")
    var selectedCategory by remember { mutableStateOf("All Courses") }

    val dummyCourses = listOf(
        CourseModel("CS301", "Data Structures & Algorithms", "Computer Science", "300 Level", "Dr. Henderson", 0.85f),
        CourseModel("MATH204", "Linear Algebra II", "Mathematics", "200 Level", "Prof. Sarah Jenkins", 0.92f),
        CourseModel("ENG101", "Academic Writing", "General Studies", "100 Level", "Leo Whitlock", 0.74f)
    )

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(title = "Courses")
        },
        bottomBar = {
            AttendEaseBottomBar(
                currentRoute = Screen.Courses.route,
                items = listOf(
                    NavigationItem("Dashboard", Icons.Default.Dashboard, Screen.AdminDashboard.route),
                    NavigationItem("Students", Icons.Default.People, Screen.Students.route),
                    NavigationItem("Courses", Icons.Default.Book, Screen.Courses.route),
                    NavigationItem("Settings", Icons.Default.Settings, "settings")
                ),
                onNavigate = { route -> navController.navigate(route) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddCourse.route) },
                containerColor = Color(0xFF006F62),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Course")
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
                placeholder = { Text("Search courses, codes, or lecturers...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.Gray) },
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

            // Course List
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(dummyCourses) { course ->
                    AdminCourseCard(course)
                }
            }
        }
    }
}

@Composable
fun AdminCourseCard(course: CourseModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = course.code,
                    color = Color(0xFF006F62),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
                }
            }
            
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("DEPARTMENT", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(course.department, style = MaterialTheme.typography.bodySmall)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("LEVEL", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(course.level, style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Text("ASSIGNED LECTURER", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color(0xFF000066)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            course.lecturer?.split(" ")?.joinToString("") { it.take(1) } ?: "?",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(Spacing.base))
                Text(course.lecturer ?: "Unassigned", style = MaterialTheme.typography.bodyMedium)
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            LinearProgressIndicator(
                progress = { course.attendance },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF006F62),
                trackColor = Color(0xFFEEEEEE)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Average Attendance", style = MaterialTheme.typography.labelSmall, color = Color(0xFF006F62), fontWeight = FontWeight.Bold)
                Text("${(course.attendance * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color(0xFF006F62), fontWeight = FontWeight.Bold)
            }
        }
    }
}
