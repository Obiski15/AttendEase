package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.AttendEaseConfirmDialog
import com.example.attendease.ui.components.AttendEaseErrorDialog
import com.example.attendease.ui.components.ListSkeleton
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.viewModel.LecturerViewModel
import com.example.attendease.dto.response.LecturerResponse
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturersScreen(
    navController: NavController,
    viewModel: LecturerViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val viewModelUiState by viewModel.uiState.collectAsState()
    val lecturers = viewModelUiState.lecturers
    val isLoading = viewModelUiState.isLoading
    val error = viewModelUiState.error

    AttendEaseErrorDialog(errorMessage = error, onDismiss = { viewModel.clearError() })

    LaunchedEffect(Unit) {
        viewModel.loadLecturers(refresh = false)
    }

    val filteredLecturers = lecturers.filter {
        val name = it.user?.name ?: ""
        val email = it.user?.email ?: ""
        val staffId = it.staffId ?: ""
        name.contains(searchQuery, ignoreCase = true) ||
                email.contains(searchQuery, ignoreCase = true) ||
                staffId.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(title = "Lecturers")
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.ADMIN,
                currentRoute = Screen.Lecturers.route,
                navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddLecturer.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Lecturer")
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
                    .padding(Spacing.md),
                placeholder = { Text("Search by name or staff ID...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            // Loading state
            if (isLoading && lecturers.isEmpty()) {
                ListSkeleton()
            } else {
                if (filteredLecturers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty()) "No lecturers available." else "No search results found.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    // Lecturer List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        itemsIndexed(filteredLecturers, key = { _, it -> it.userId }) { index, lecturer ->
                            if (index == filteredLecturers.size - 1) {
                                LaunchedEffect(index) {
                                    viewModel.loadMore()
                                }
                            }
                            LecturerCard(
                                lecturer = lecturer,
                                onCardClick = {
                                    navController.navigate(Screen.LecturerDetail.createRoute(lecturer.userId))
                                },
                                onEditClick = {
                                    navController.navigate(Screen.EditLecturer.createRoute(lecturer.userId))
                                },
                                onDeleteClick = {
                                    viewModel.deleteLecturer(lecturer.userId)
                                }
                            )
                        }
                        if (isLoading && lecturers.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LecturerCard(
    lecturer: LecturerResponse,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lecturer.user?.name ?: "User",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = lecturer.user?.email ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Staff ID: ${lecturer.staffId ?: "N/A"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            showMenu = false
                            onEditClick()
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            showDeleteConfirm = true
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp)) }
                    )
                }
            }
        }
    }

    AttendEaseConfirmDialog(
        show = showDeleteConfirm,
        title = "Delete Lecturer",
        message = "Are you sure you want to delete the lecturer account for '${lecturer.user?.name ?: "Lecturer"}'? This action cannot be undone.",
        onConfirm = {
            showDeleteConfirm = false
            onDeleteClick()
        },
        onDismiss = { showDeleteConfirm = false }
    )
}