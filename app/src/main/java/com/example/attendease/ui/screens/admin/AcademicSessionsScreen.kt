package com.example.attendease.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.AttendEaseDropdown
import com.example.attendease.ui.components.AttendEaseFormField
import com.example.attendease.ui.components.AttendEaseConfirmDialog
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.viewModel.AcademicSessionViewModel
import com.example.attendease.dto.response.AcademicSessionResponse
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicSessionsScreen(
    navController: NavController,
    viewModel: AcademicSessionViewModel = koinViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessions by viewModel.sessions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var sessionToEdit by remember { mutableStateOf<AcademicSessionResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadSessions()
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            val message = if (showEditDialog) "Academic Session updated successfully!" else "Academic Session created successfully!"
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetSaveState()
            showCreateDialog = false
            showEditDialog = false
        }
    }

    LaunchedEffect(error) {
        error?.let { err ->
            android.widget.Toast.makeText(context, "Error: $err", android.widget.Toast.LENGTH_LONG).show()
            viewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                title = "Academic Sessions",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.ADMIN,
                currentRoute = Screen.AcademicSessions.route,
                navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(0xFF006F62),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Session")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && sessions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (sessions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(Spacing.base))
                        Text("No academic sessions registered", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(top = Spacing.base, bottom = 80.dp)
                ) {
                    items(sessions) { session ->
                        AcademicSessionCard(
                            session = session,
                            onActivate = { viewModel.activateSession(session.id) },
                            onDelete = { viewModel.deleteSession(session.id) },
                            onEdit = {
                                sessionToEdit = session
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showCreateDialog) {
            AddAcademicSessionDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, semester, active, start, end ->
                    viewModel.createSession(name, semester, active, start, end)
                }
            )
        }

        if (showEditDialog) {
            sessionToEdit?.let { session ->
                EditAcademicSessionDialog(
                    session = session,
                    onDismiss = { showEditDialog = false },
                    onConfirm = { name, semester, active, start, end ->
                        viewModel.updateSession(session.id, name, semester, active, start, end)
                    }
                )
            }
        }
    }
}

@Composable
fun AcademicSessionCard(
    session: AcademicSessionResponse,
    onActivate: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
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
                Column {
                    Text(
                        text = session.sessionName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${session.semester} Semester",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    if (session.startDate.isNotBlank() && session.endDate.isNotBlank()) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "${session.startDate} to ${session.endDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Session",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Surface(
                        color = if (session.isActive) Color(0xFFE0F2F1) else Color(0xFFEEEEEE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (session.isActive) "ACTIVE" else "INACTIVE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (session.isActive) Color(0xFF006F62) else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            if (!session.isActive) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Button(
                        onClick = onActivate,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006F62)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text("Activate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text("Delete", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFE0F2F1).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF006F62), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = "This is the active semester for all attendance check-ins.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF004D40)
                        )
                    }
                }
            }
        }
    }

    AttendEaseConfirmDialog(
        show = showDeleteConfirm,
        title = "Delete Academic Session",
        message = "Are you sure you want to delete academic session '${session.sessionName}' (${session.semester} Semester)? This action cannot be undone.",
        onConfirm = {
            showDeleteConfirm = false
            onDelete()
        },
        onDismiss = { showDeleteConfirm = false }
    )
}

@Composable
fun AddAcademicSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, semester: String, isActive: Boolean, startDate: String, endDate: String) -> Unit
) {
    var sessionName by remember { mutableStateOf("") }
    var selectedSemester by remember { mutableStateOf("First") }
    var isActive by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Academic Session",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                AttendEaseFormField(
                    label = "Session Name",
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    placeholder = "e.g. 2023/2024",
                    trailingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = Color.LightGray) }
                )

                AttendEaseDropdown(
                    label = "Semester",
                    value = selectedSemester,
                    options = listOf("First", "Second"),
                    onOptionSelected = { selectedSemester = it }
                )

                AttendEaseFormField(
                    label = "Start Date (YYYY-MM-DD)",
                    value = startDate,
                    onValueChange = { startDate = it },
                    placeholder = "e.g. 2026-09-01",
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.LightGray) }
                )

                AttendEaseFormField(
                    label = "End Date (YYYY-MM-DD)",
                    value = endDate,
                    onValueChange = { endDate = it },
                    placeholder = "e.g. 2027-01-30",
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.LightGray) }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs)
                ) {
                    Checkbox(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF006F62))
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("Set as active session immediately", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(sessionName, selectedSemester, isActive, startDate, endDate) },
                enabled = sessionName.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006F62))
            ) {
                Text("Create", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun EditAcademicSessionDialog(
    session: AcademicSessionResponse,
    onDismiss: () -> Unit,
    onConfirm: (name: String, semester: String, isActive: Boolean, startDate: String, endDate: String) -> Unit
) {
    var sessionName by remember { mutableStateOf(session.sessionName) }
    var selectedSemester by remember { mutableStateOf(session.semester) }
    var isActive by remember { mutableStateOf(session.isActive) }
    var startDate by remember { mutableStateOf(session.startDate) }
    var endDate by remember { mutableStateOf(session.endDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Academic Session",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                AttendEaseFormField(
                    label = "Session Name",
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    placeholder = "e.g. 2023/2024",
                    trailingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = Color.LightGray) }
                )

                AttendEaseDropdown(
                    label = "Semester",
                    value = selectedSemester,
                    options = listOf("First", "Second"),
                    onOptionSelected = { selectedSemester = it }
                )

                AttendEaseFormField(
                    label = "Start Date (YYYY-MM-DD)",
                    value = startDate,
                    onValueChange = { startDate = it },
                    placeholder = "e.g. 2026-09-01",
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.LightGray) }
                )

                AttendEaseFormField(
                    label = "End Date (YYYY-MM-DD)",
                    value = endDate,
                    onValueChange = { endDate = it },
                    placeholder = "e.g. 2027-01-30",
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.LightGray) }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs)
                ) {
                    Checkbox(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF006F62))
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("Set as active session immediately", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(sessionName, selectedSemester, isActive, startDate, endDate) },
                enabled = sessionName.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006F62))
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

