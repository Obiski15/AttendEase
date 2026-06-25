package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.layout.*
import com.example.attendease.ui.components.AttendEaseErrorDialog
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
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import com.example.attendease.viewModel.CourseViewModel
import com.example.attendease.viewModel.DepartmentViewModel
import com.example.attendease.viewModel.CourseAssignmentViewModel
import com.example.attendease.dto.response.CourseResponse
import com.example.attendease.ui.components.ListSkeleton
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    navController: NavController,
    courseViewModel: CourseViewModel = koinViewModel(),
    departmentViewModel: DepartmentViewModel = koinViewModel(),
    assignmentViewModel: CourseAssignmentViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val courses by courseViewModel.courses.collectAsState()
    val departments by departmentViewModel.departments.collectAsState()
    val assignmentState by assignmentViewModel.uiState.collectAsState()
    
    val isLoading by courseViewModel.isLoading.collectAsState()
    val error by courseViewModel.error.collectAsState()
    AttendEaseErrorDialog(errorMessage = error, onDismiss = { courseViewModel.clearError() })


    var selectedDept by remember { mutableStateOf("ALL") }

    LaunchedEffect(Unit) {
        courseViewModel.loadCourses()
        departmentViewModel.loadDepartments()
        assignmentViewModel.loadData()
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
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
                placeholder = { Text("Search courses or codes...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            // Department Filter Chips
            LazyRow(
                modifier = Modifier.padding(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                item {
                    FilterChip(
                        selected = selectedDept == "ALL",
                        onClick = { selectedDept = "ALL" },
                        label = { Text("All Departments") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
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
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))

            if (isLoading || assignmentState.isLoading) {
                ListSkeleton()
            } else if (filteredCourses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No courses found", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredCourses) { course ->
                            val deptName = departments.find { it.id == course.departmentId }?.name ?: "Unknown"
                            val assignedLecturer = assignmentState.assignments.find { it.courseId == course.id }?.lecturerName
                            
                            AdminCourseCard(
                                course = course,
                                departmentName = deptName,
                                assignedLecturer = assignedLecturer,
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
    assignedLecturer: String?,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Course", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    Text("DEPARTMENT", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(departmentName, style = MaterialTheme.typography.bodySmall)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("CREDITS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${course.creditUnits} Units", style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Text("ASSIGNED LECTURER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val initial = assignedLecturer?.firstOrNull()?.uppercase() ?: "?"
                        Text(
                            initial,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(Spacing.base))
                Text(assignedLecturer ?: "Not Available", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

