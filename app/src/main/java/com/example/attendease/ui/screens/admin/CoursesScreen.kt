package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.layout.*
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.lazy.LazyColumn
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.lazy.LazyRow
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.lazy.items
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.shape.CircleShape
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.material.icons.Icons
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.material.icons.filled.*
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.material3.*
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.runtime.*
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.Alignment
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.Modifier
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.draw.clip
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.graphics.Color
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.text.font.FontWeight
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.unit.dp
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.compose.ui.unit.sp
import com.example.attendease.ui.components.AttendEaseErrorDialog
import androidx.navigation.NavController
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.ui.components.AttendEaseErrorDialog
import org.koin.androidx.compose.koinViewModel
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.viewModel.CourseViewModel
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.viewModel.DepartmentViewModel
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.dto.response.CourseResponse
import com.example.attendease.ui.components.AttendEaseErrorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    navController: NavController,
    courseViewModel: CourseViewModel = koinViewModel(),
    departmentViewModel: DepartmentViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val courses by courseViewModel.courses.collectAsState()
    val departments by departmentViewModel.departments.collectAsState()
    val isLoading by courseViewModel.isLoading.collectAsState()
    val error by courseViewModel.error.collectAsState()
    AttendEaseErrorDialog(errorMessage = error, onDismiss = { courseViewModel.clearError() })


    var selectedDept by remember { mutableStateOf("ALL") }

    LaunchedEffect(Unit) {
        courseViewModel.loadCourses()
        departmentViewModel.loadDepartments()
    }

    val filteredCourses = courses.filter { course ->
        val titleMatch = course.title.contains(searchQuery, ignoreCase = true)
        val codeMatch = course.courseCode.contains(searchQuery, ignoreCase = true)
        val deptMatch = selectedDept == "ALL" || course.departmentId == selectedDept
        (titleMatch || codeMatch) && deptMatch
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(title = "Courses")
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.ADMIN,
                currentRoute = Screen.Courses.route,
                navController,
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
                placeholder = { Text("Search courses or codes...", color = Color.Gray) },
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
                item {
                    FilterChip(
                        selected = selectedDept == "ALL",
                        onClick = { selectedDept = "ALL" },
                        label = { Text("All Courses") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF000066),
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(departments) { dept ->
                    FilterChip(
                        selected = selectedDept == dept.id,
                        onClick = { selectedDept = dept.id },
                        label = { Text(dept.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF000066),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                

                if (filteredCourses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Text("No courses found.", color = Color.Gray)
                    }
                } else {
                    // Course List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredCourses) { course ->
                            val deptName = departments.find { it.id == course.departmentId }?.name ?: "Unknown"
                            AdminCourseCard(
                                course = course,
                                departmentName = deptName,
                                onEditClick = {
                                    navController.navigate(Screen.EditCourse.createRoute(course.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCourseCard(
    course: CourseResponse,
    departmentName: String,
    onEditClick: () -> Unit
) {
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
                    text = course.courseCode,
                    color = Color(0xFF006F62),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Course", tint = Color.Gray)
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
                    Text(departmentName, style = MaterialTheme.typography.bodySmall)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("CREDITS", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("${course.creditUnits} Units", style = MaterialTheme.typography.bodySmall)
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
                            "?", // Lecturer assigned info not in CourseResponse directly
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(Spacing.base))
                Text("Not Available", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

