package com.example.attendease.ui.screens.lecturer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.components.CornerBracket
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.utils.QrCodeGenerator
import com.example.attendease.viewModel.LecturerSessionViewModel
import kotlinx.coroutines.delay
import androidx.compose.material3.MaterialTheme

@Composable
fun LecturerActiveSessionScreen(
    navController: NavController,
    sessionViewModel: LecturerSessionViewModel
) {
    val activeSession by sessionViewModel.activeSession.collectAsState()
    val activeCourseTitle by sessionViewModel.activeCourseTitle.collectAsState()
    val records by sessionViewModel.checkedInRecords.collectAsState()
    val isLoading by sessionViewModel.isLoading.collectAsState()
    val error by sessionViewModel.error.collectAsState()

    var timeLeftString by remember { mutableStateOf("Active") }

    val qrBitmap = remember(activeSession?.sessionCode) {
        activeSession?.sessionCode?.let { code ->
            QrCodeGenerator.generateQrCode(code, 400)
        }
    }

    LaunchedEffect(activeSession, isLoading) {
        if (activeSession == null && !isLoading) {
            navController.popBackStack()
            return@LaunchedEffect
        }
        val expiresAtStr = activeSession?.expiresAt
        if (expiresAtStr != null) {
            val cleanStr = expiresAtStr.replace(" ", "T")
            val expiryInstant = try {
                if (!cleanStr.contains("Z") && !cleanStr.contains("+") && cleanStr.indexOf('T') > 0) {
                    java.time.LocalDateTime.parse(cleanStr).toInstant(java.time.ZoneOffset.UTC)
                } else {
                    java.time.Instant.parse(cleanStr)
                }
            } catch (e: Exception) {
                null
            }

            if (expiryInstant != null) {
                while (true) {
                    val diffMs = expiryInstant.toEpochMilli() - java.time.Instant.now().toEpochMilli()
                    if (diffMs <= 0) {
                        timeLeftString = "Expired"
                        break
                    }
                    val totalSecs = diffMs / 1000
                    val mins = totalSecs / 60
                    val secs = totalSecs % 60
                    timeLeftString = String.format("%02d:%02d remaining", mins, secs)
                    delay(1000)
                }
            } else {
                timeLeftString = "Active"
            }
        } else {
            timeLeftString = "Active"
        }
    }

    // Trigger polling immediately when active session is set
    LaunchedEffect(activeSession) {
        activeSession?.id?.let {
            sessionViewModel.startPollingRecords(it)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            sessionViewModel.stopPollingRecords()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AttendEaseTopAppBar()
        },
        bottomBar = {
            Box(modifier = Modifier.padding(Spacing.lg)) {
                Button(
                    onClick = {
                        sessionViewModel.closeActiveSession {}
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.StopCircle,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = "End Session Now",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            if (activeSession == null) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No active session found.", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Go Back")
                            }
                        }
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(Spacing.base))
                    Text(
                        text = activeCourseTitle ?: "Scan to Attend",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = "Live Session Active",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(Spacing.lg),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(Spacing.xs),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (qrBitmap != null) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "Session QR Code",
                                    modifier = Modifier.size(160.dp)
                                )
                            } else {
                                Text(
                                    text = activeSession?.sessionCode ?: "...",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 2.sp
                                )
                            }
                        }

                        CornerBracket(Alignment.TopStart)
                        CornerBracket(Alignment.TopEnd)
                        CornerBracket(Alignment.BottomStart)
                        CornerBracket(Alignment.BottomEnd)
                    }
                }

                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        @Suppress("DEPRECATION")
                        val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                        val context = LocalContext.current
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeSession?.sessionCode ?: "...",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            IconButton(
                                onClick = {
                                    activeSession?.sessionCode?.let {
                                        clipboardManager.setText(AnnotatedString(it))
                                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Session Code",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.base),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(
                                text = timeLeftString,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = "SESSION DETAILS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = "Attendance Window Open",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Date: ${activeSession?.sessionDate ?: "Today"}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "GEOFENCING BOUNDARY",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (activeSession?.geofencingEnabled == true) "Enabled (${activeSession?.radiusMeters}m)" else "Disabled",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (activeSession?.geofencingEnabled == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Students Checked In",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${records.size}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val total = activeSession?.totalStudents ?: 0
                                    Text(
                                        text = if (total > 0) " / $total checked in" else " checked in",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.sm))

                            LinearProgressIndicator(
                                progress = {
                                    val total = activeSession?.totalStudents ?: 0
                                    if (total > 0 && records.isNotEmpty()) {
                                        (records.size.toFloat() / total).coerceAtMost(1.0f)
                                    } else {
                                        0f
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                    }
                }

                if (records.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "Checked-in Records",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    items(records) { record ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.md).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Student: ${record.studentId?.take(8) ?: "..."}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Time: ${com.example.attendease.utils.DateUtils.parseIsoTimeToDisplay(record.checkInTime)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = record.status ?: "PRESENT",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }
            }
        }
    }
}
