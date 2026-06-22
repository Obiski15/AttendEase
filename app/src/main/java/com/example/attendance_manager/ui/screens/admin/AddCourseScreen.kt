package com.example.attendance_manager.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.attendance_manager.ui.components.AttendEaseDropdown
import com.example.attendance_manager.ui.components.AttendEaseFormField
import com.example.attendance_manager.ui.components.AttendEaseTopAppBar
import com.example.attendance_manager.ui.components.SuccessModal
import com.example.attendance_manager.ui.theme.Spacing

@Composable
fun AddCourseScreen(navController: NavController) {
    var courseCode by remember { mutableStateOf("") }
    var courseTitle by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var creditUnits by remember { mutableIntStateOf(3) }
    var showSuccessModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                title = "Add Course",
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                showBadge = false
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    // Info Banner
                    Surface(
                        color = Color(0xFFF1F3F4),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(Spacing.md)) {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF006F62))
                            Spacer(modifier = Modifier.width(Spacing.md))
                            Column {
                                Text("Course Registration", fontWeight = FontWeight.Bold, color = Color(0xFF000066))
                                Text(
                                    "Registering a new course will make it available for semester scheduling and attendance tracking.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    // Image Banner Placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF000033)),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            "Academic Session 2023/2024",
                            color = Color.White,
                            modifier = Modifier.padding(Spacing.md),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                            .padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        AttendEaseFormField(
                            label = "Course Code",
                            value = courseCode,
                            onValueChange = { courseCode = it },
                            placeholder = "e.g. CSC401",
                            trailingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = Color.LightGray) }
                        )
                        AttendEaseFormField(
                            label = "Course Title",
                            value = courseTitle,
                            onValueChange = { courseTitle = it },
                            placeholder = "e.g. Artificial Intelligence",
                            trailingIcon = { Icon(Icons.Default.Title, contentDescription = null, tint = Color.LightGray) }
                        )
                        
                        AttendEaseDropdown(
                            label = "Department",
                            value = department,
                            options = listOf("Computer Science", "Engineering", "Mathematics"),
                            onOptionSelected = { department = it }
                        )

                        AttendEaseDropdown(
                            label = "Level",
                            value = level,
                            options = listOf("100 Level", "200 Level", "300 Level", "400 Level", "500 Level"),
                            onOptionSelected = { level = it }
                        )

                        Column {
                            Text("Credit Units", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedIconButton(onClick = { if (creditUnits > 0) creditUnits-- }, shape = RoundedCornerShape(8.dp)) {
                                    Icon(Icons.Default.Remove, contentDescription = null)
                                }
                                Surface(
                                    modifier = Modifier.weight(1f).height(40.dp).padding(horizontal = 8.dp),
                                    color = Color(0xFFF1F3F4),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(creditUnits.toString(), fontWeight = FontWeight.Bold)
                                    }
                                }
                                OutlinedIconButton(onClick = { creditUnits++ }, shape = RoundedCornerShape(8.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                }
                            }
                        }

                        // Verification Note
                        Surface(
                            color = Color(0xFFE0F2F1),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF006F62), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(Spacing.base))
                                Text(
                                    "Verification: This course will be categorized under the Science Faculty.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = Color(0xFF004D40)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    Button(
                        onClick = { showSuccessModal = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006F62)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(Spacing.base))
                        Text("Save Course", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }

            if (showSuccessModal) {
                SuccessModal(
                    title = "Course Added",
                    message = "The course has been successfully added to the system.",
                    onContinue = {
                        showSuccessModal = false
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
