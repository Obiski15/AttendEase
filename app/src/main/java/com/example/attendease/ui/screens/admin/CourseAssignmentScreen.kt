package com.example.attendease.ui.screens.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.AttendEaseDropdown
import com.example.attendease.ui.components.AttendEaseConfirmDialog
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.viewModel.CourseAssignmentViewModel
import com.example.attendease.state.CourseAssignmentUiModel
import com.example.attendease.dto.response.CourseResponse
import com.example.attendease.dto.response.LecturerResponse
import com.example.attendease.dto.response.AcademicSessionResponse
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseAssignmentScreen(
    navController: NavController,
    viewModel: CourseAssignmentViewModel = koinViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Departments") }

    // Dialog state variables
    var showAssignDialog by remember { mutableStateOf(false) }
    var isReassign by remember { mutableStateOf(false) }
    var reassignOldAssignmentId by remember { mutableStateOf<String?>(null) }
    var selectedCourseId by remember { mutableStateOf<String?>(null) }
    var selectedCourseCode by remember { mutableStateOf<String?>(null) }
    var selectedCourseTitle by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    LaunchedEffect(uiState.saveSuccess, uiState.deleteSuccess) {
        if (uiState.saveSuccess) {
            android.widget.Toast.makeText(context, "Assignment saved successfully!", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetSaveState()
            showAssignDialog = false
        }
        if (uiState.deleteSuccess) {
            android.widget.Toast.makeText(context, "Assignment removed!", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetSaveState()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { err ->
            android.widget.Toast.makeText(context, "Error: $err", android.widget.Toast.LENGTH_LONG).show()
            viewModel.resetSaveState()
        }
    }

    // Dynamic categories from fetched department lists could be added, but for now we filter in code
    val categories = listOf("All Departments", "Computer Science", "Engineering", "Mathematics")

    val filteredAssignments = uiState.assignments.filter { assignment ->
        val matchesSearch = assignment.courseCode.contains(searchQuery, ignoreCase = true) ||
                assignment.courseTitle.contains(searchQuery, ignoreCase = true) ||
                assignment.lecturerName.contains(searchQuery, ignoreCase = true)
        
        val matchesCategory = selectedCategory == "All Departments" ||
                assignment.lecturerRole.contains(selectedCategory, ignoreCase = true) ||
                assignment.courseTitle.contains(selectedCategory, ignoreCase = true)

        matchesSearch && matchesCategory
    }

    val filteredUnassigned = uiState.unassignedCourses.filter { course ->
        val matchesSearch = course.courseCode.contains(searchQuery, ignoreCase = true) ||
                course.title.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == "All Departments" ||
                course.title.contains(selectedCategory, ignoreCase = true)

        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(title = "Course Assignments")
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.ADMIN,
                currentRoute = Screen.CourseAssignment.route,
                navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isReassign = false
                    reassignOldAssignmentId = null
                    selectedCourseId = null
                    selectedCourseCode = null
                    selectedCourseTitle = null
                    showAssignDialog = true
                },
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

            // Assignments & Unassigned courses list
            if (uiState.isLoading && uiState.assignments.isEmpty() && uiState.unassignedCourses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (filteredAssignments.isEmpty() && filteredUnassigned.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(Spacing.base))
                        Text("No courses or assignments found", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (filteredAssignments.isNotEmpty()) {
                        item {
                            Text(
                                text = "Assigned Courses",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = Spacing.xs)
                            )
                        }
                        items(filteredAssignments) { assignment ->
                            AssignmentCard(
                                assignment = assignment,
                                onReassign = {
                                    isReassign = true
                                    reassignOldAssignmentId = assignment.id
                                    selectedCourseId = assignment.courseId
                                    selectedCourseCode = assignment.courseCode
                                    selectedCourseTitle = assignment.courseTitle
                                    showAssignDialog = true
                                },
                                onRemove = {
                                    viewModel.removeAssignment(assignment.id)
                                }
                            )
                        }
                    }

                    if (filteredUnassigned.isNotEmpty()) {
                        item {
                            Text(
                                text = "Unassigned Courses",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.xs)
                            )
                        }
                        items(filteredUnassigned) { course ->
                            UnassignedCourseCard(
                                course = course,
                                onAssign = {
                                    isReassign = false
                                    reassignOldAssignmentId = null
                                    selectedCourseId = course.id
                                    selectedCourseCode = course.courseCode
                                    selectedCourseTitle = course.title
                                    showAssignDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showAssignDialog) {
            AssignLecturerDialog(
                isReassign = isReassign,
                preselectedCourseCode = selectedCourseCode,
                preselectedCourseTitle = selectedCourseTitle,
                preselectedCourseId = selectedCourseId,
                unassignedCourses = uiState.unassignedCourses,
                lecturers = uiState.lecturers,
                academicSessions = uiState.academicSessions,
                onDismiss = { showAssignDialog = false },
                onConfirm = { courseId, lecturerId, sessionId ->
                    if (isReassign) {
                        reassignOldAssignmentId?.let { oldId ->
                            viewModel.reassignLecturer(
                                oldAssignmentId = oldId,
                                courseId = courseId,
                                lecturerId = lecturerId,
                                academicSessionId = sessionId
                            )
                        }
                    } else {
                        viewModel.assignLecturer(
                            courseId = courseId,
                            lecturerId = lecturerId,
                            academicSessionId = sessionId
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun AssignmentCard(
    assignment: CourseAssignmentUiModel,
    onReassign: () -> Unit,
    onRemove: () -> Unit
) {
    var showRemoveConfirm by remember { mutableStateOf(false) }

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
                Text(
                    text = assignment.sessionName,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
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
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column {
                        Text(assignment.lecturerName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(assignment.lecturerRole, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Button(
                    onClick = onReassign,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2EBF2), contentColor = Color(0xFF006F62)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Reassign", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { showRemoveConfirm = true },
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

    AttendEaseConfirmDialog(
        show = showRemoveConfirm,
        title = "Remove Assignment",
        message = "Are you sure you want to remove the assignment of '${assignment.lecturerName}' to '${assignment.courseCode}'?",
        confirmButtonText = "Remove",
        onConfirm = {
            showRemoveConfirm = false
            onRemove()
        },
        onDismiss = { showRemoveConfirm = false }
    )
}

@Composable
fun UnassignedCourseCard(
    course: CourseResponse,
    onAssign: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
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
            Text(course.courseCode, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(course.title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(Spacing.md))
            Button(
                onClick = onAssign,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000066)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Quick Assign", modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignLecturerDialog(
    isReassign: Boolean,
    preselectedCourseCode: String?,
    preselectedCourseTitle: String?,
    preselectedCourseId: String?,
    unassignedCourses: List<CourseResponse>,
    lecturers: List<LecturerResponse>,
    academicSessions: List<AcademicSessionResponse>,
    onDismiss: () -> Unit,
    onConfirm: (courseId: String, lecturerId: String, academicSessionId: String) -> Unit
) {
    var selectedCourseName by remember { mutableStateOf("") }
    var selectedLecturerName by remember { mutableStateOf("") }
    var selectedSessionName by remember { mutableStateOf("") }

    val defaultSession = academicSessions.find { it.isActive }
    LaunchedEffect(defaultSession) {
        if (defaultSession != null && selectedSessionName.isEmpty()) {
            selectedSessionName = "${defaultSession.sessionName} (${defaultSession.semester})"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isReassign) "Reassign Lecturer" else "Assign Lecturer to Course",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isReassign) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Course",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                        ) {
                            Text(
                                text = "${preselectedCourseCode ?: ""} - ${preselectedCourseTitle ?: ""}",
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    val courseOptions = unassignedCourses.map { "${it.courseCode} - ${it.title}" }
                    AttendEaseDropdown(
                        label = "Course",
                        value = selectedCourseName,
                        options = courseOptions,
                        onOptionSelected = { selectedCourseName = it }
                    )
                }

                val lecturerOptions = lecturers.map { it.user?.name ?: "Staff ID: ${it.staffId}" }
                AttendEaseDropdown(
                    label = "Lecturer",
                    value = selectedLecturerName,
                    options = lecturerOptions,
                    onOptionSelected = { selectedLecturerName = it }
                )

                val sessionOptions = academicSessions.map { "${it.sessionName} (${it.semester})" }
                AttendEaseDropdown(
                    label = "Academic Session",
                    value = selectedSessionName,
                    options = sessionOptions,
                    onOptionSelected = { selectedSessionName = it }
                )
            }
        },
        confirmButton = {
            val isConfirmEnabled = (isReassign || selectedCourseName.isNotEmpty()) &&
                    selectedLecturerName.isNotEmpty() &&
                    selectedSessionName.isNotEmpty()
            Button(
                onClick = {
                    val courseId = if (isReassign) {
                        preselectedCourseId ?: ""
                    } else {
                        unassignedCourses.find { "${it.courseCode} - ${it.title}" == selectedCourseName }?.id ?: ""
                    }
                    val lecturerId = lecturers.find { (it.user?.name ?: "Staff ID: ${it.staffId}") == selectedLecturerName }?.userId ?: ""
                    val sessionId = academicSessions.find { "${it.sessionName} (${it.semester})" == selectedSessionName }?.id ?: ""
                    onConfirm(courseId, lecturerId, sessionId)
                },
                enabled = isConfirmEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006F62))
            ) {
                Text("Confirm", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
