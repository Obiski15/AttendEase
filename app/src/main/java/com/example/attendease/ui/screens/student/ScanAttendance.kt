package com.example.attendease.ui.screens.student

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanAttendanceScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AttendEaseTopAppBar(
                containerColor = Color.White
            )
        },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.STUDENT,
                currentRoute = Screen.ScanAttendance.route,
                navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Camera Preview Placeholder (Blurred background effect)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.3f))
            )

            // Scanner Overlay
            ScannerOverlay(
                modifier = Modifier.fillMaxSize(),
                onScanAreaMeasured = {}
            )

            // UI Elements on top of the overlay
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                Surface(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = Spacing.lg)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.md),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Scan QR Code",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Position the lecturer's QR code within the frame",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.5f)
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.FlashlightOn, contentDescription = "Flashlight", tint = Color.Black)
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(Spacing.lg))
                    
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Capture", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(Spacing.lg))
                    
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.5f)
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Image, contentDescription = "Gallery", tint = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    onScanAreaMeasured: (Rect) -> Unit
) {
    val strokeColor = MaterialTheme.colorScheme.secondary
    
    Canvas(modifier = modifier) {
        val scanAreaSize = 260.dp.toPx()
        val left = (size.width - scanAreaSize) / 2
        val top = (size.height - scanAreaSize) / 2
        val rect = Rect(left, top, left + scanAreaSize, top + scanAreaSize)

        // Draw darkened background with a hole
        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination: The whole screen darkened
            drawRect(Color.Black.copy(alpha = 0.5f))

            // Source: The transparent hole
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(rect.left, rect.top),
                size = Size(rect.width, rect.height),
                cornerRadius = CornerRadius(24.dp.toPx()),
                blendMode = BlendMode.Clear
            )

            restoreToCount(checkPoint)
        }

        // Draw Corner Brackets
        val bracketLength = 40.dp.toPx()
        val bracketThickness = 4.dp.toPx()
        val cornerRadius = 24.dp.toPx()

        // Top Left
        drawPath(
            path = Path().apply {
                moveTo(rect.left, rect.top + bracketLength)
                lineTo(rect.left, rect.top + cornerRadius)
                arcTo(
                    rect = Rect(rect.left, rect.top, rect.left + cornerRadius * 2, rect.top + cornerRadius * 2),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(rect.left + bracketLength, rect.top)
            },
            color = strokeColor,
            style = Stroke(width = bracketThickness, cap = StrokeCap.Round)
        )

        // Top Right
        drawPath(
            path = Path().apply {
                moveTo(rect.right - bracketLength, rect.top)
                lineTo(rect.right - cornerRadius, rect.top)
                arcTo(
                    rect = Rect(rect.right - cornerRadius * 2, rect.top, rect.right, rect.top + cornerRadius * 2),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(rect.right, rect.top + bracketLength)
            },
            color = strokeColor,
            style = Stroke(width = bracketThickness, cap = StrokeCap.Round)
        )

        // Bottom Left
        drawPath(
            path = Path().apply {
                moveTo(rect.left, rect.bottom - bracketLength)
                lineTo(rect.left, rect.bottom - cornerRadius)
                arcTo(
                    rect = Rect(rect.left, rect.bottom - cornerRadius * 2, rect.left + cornerRadius * 2, rect.bottom),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(rect.left + bracketLength, rect.bottom)
            },
            color = strokeColor,
            style = Stroke(width = bracketThickness, cap = StrokeCap.Round)
        )

        // Bottom Right
        drawPath(
            path = Path().apply {
                moveTo(rect.right - bracketLength, rect.bottom)
                lineTo(rect.right - cornerRadius, rect.bottom)
                arcTo(
                    rect = Rect(rect.right - cornerRadius * 2, rect.bottom - cornerRadius * 2, rect.right, rect.bottom),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(rect.right, rect.bottom - bracketLength)
            },
            color = strokeColor,
            style = Stroke(width = bracketThickness, cap = StrokeCap.Round)
        )
    }
}
