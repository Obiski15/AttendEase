package com.example.attendease.ui.screens.student

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.components.AttendEaseBottomBar
import com.example.attendease.ui.components.AttendEaseTopAppBar
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.ui.theme.Spacing
import com.example.attendease.utils.QrCodeAnalyzer
import com.example.attendease.viewModel.AttendanceViewModel
import com.google.android.gms.location.LocationServices
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanAttendanceScreen(
    navController: NavController,
    attendanceViewModel: AttendanceViewModel = koinViewModel(),
    sessionManager: com.example.attendease.data.session.SessionManager = org.koin.compose.koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val isLoading by attendanceViewModel.isLoading.collectAsState()
    val error by attendanceViewModel.error.collectAsState()
    val checkInSuccess by attendanceViewModel.checkInSuccess.collectAsState()

    var showManualInput by remember { mutableStateOf(false) }
    var codeInput by remember { mutableStateOf("") }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
            val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            hasLocationPermission = fine || coarse
        }
    )

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (!hasCameraPermission) permissionsToRequest.add(Manifest.permission.CAMERA)
        if (!hasLocationPermission) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun proceedWithLocationAndCheckIn(cleanCode: String) {
        if (hasLocationPermission) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val lat = location?.latitude ?: 6.5244 
                    val lon = location?.longitude ?: 3.3792
                    attendanceViewModel.checkIn(cleanCode, lat, lon)
                }.addOnFailureListener {
                    attendanceViewModel.checkIn(cleanCode, 6.5244, 3.3792)
                }
            } catch (e: SecurityException) {
                attendanceViewModel.checkIn(cleanCode, 6.5244, 3.3792)
            }
        } else {
            attendanceViewModel.checkIn(cleanCode, null, null)
        }
    }

    fun processCode(code: String) {
        val cleanCode = code.trim().uppercase()
        if (cleanCode.length < 5) return

        if (sessionManager.isBiometricEnabled()) {
            val activity = context as? androidx.fragment.app.FragmentActivity
            if (activity != null && com.example.attendease.utils.BiometricHelper.isBiometricAvailable(activity)) {
                coroutineScope.launch {
                    val passed = com.example.attendease.utils.BiometricHelper.authenticate(
                        activity = activity,
                        title = "Secure Check-in",
                        subtitle = "Verify your identity to record attendance"
                    )
                    if (passed) {
                        proceedWithLocationAndCheckIn(cleanCode)
                    }
                }
            } else {
                proceedWithLocationAndCheckIn(cleanCode)
            }
        } else {
            proceedWithLocationAndCheckIn(cleanCode)
        }
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var isTorchOn by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AttendEaseTopAppBar(containerColor = MaterialTheme.colorScheme.surface) },
        bottomBar = {
            AttendEaseBottomBar(
                UserRole.STUDENT,
                currentRoute = Screen.ScanAttendance.route,
                navController
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                            val imageAnalyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(ContextCompat.getMainExecutor(ctx), QrCodeAnalyzer { qrCodeValue ->
                                        if (!isLoading && checkInSuccess == null && error == null) { processCode(qrCodeValue) }
                                    })
                                }
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            try {
                                cameraProvider.unbindAll()
                                val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
                                cameraControl = camera.cameraControl
                            } catch (e: Exception) {}
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Camera permission is required.", color = MaterialTheme.colorScheme.surface)
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Button(onClick = { permissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA)) }) { Text("Grant Permission") }
                    }
                }
            }

            ScannerOverlay(modifier = Modifier.fillMaxSize(), onScanAreaMeasured = {})

            // Top Instructions
            Surface(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp, start = Spacing.lg, end = Spacing.lg)
            ) {
                Column(modifier = Modifier.padding(Spacing.md), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Scan QR Code", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Position the lecturer's QR code within the frame", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                }
            }

            // Torch Button and Loader perfectly centered below the scan area
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 180.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = if (isTorchOn) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        IconButton(onClick = { cameraControl?.let { isTorchOn = !isTorchOn; it.enableTorch(isTorchOn) } }) {
                            Icon(
                                imageVector = if (isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                                contentDescription = "Flashlight",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = if (isTorchOn) "Torch On" else "Torch Off",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // Bottom Manual Input Button
            Button(
                onClick = { showManualInput = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = Spacing.lg, vertical = 32.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.7f), contentColor = MaterialTheme.colorScheme.onPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Keyboard, contentDescription = null)
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text("Enter Code Manually", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showManualInput) {
        AlertDialog(
            onDismissRequest = { showManualInput = false },
            title = { Text("Enter Attendance Code", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Ask your lecturer for the code.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(Spacing.md))
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = { codeInput = it.take(10) },
                        placeholder = { Text("e.g. A3F9K2") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showManualInput = false
                        processCode(codeInput)
                    }
                ) {
                    Text("Verify")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualInput = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    checkInSuccess?.let { record ->
        AlertDialog(
            onDismissRequest = { attendanceViewModel.resetState() },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Check-In Successful", fontWeight = FontWeight.Bold) },
            text = {
                val timeStr = record.checkInTime?.substringAfter('T')?.substringBefore('.') ?: "..."
                Text(
                    text = "You have successfully checked in!\nTime: $timeStr",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        attendanceViewModel.resetState()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("OK")
                }
            }
        )
    }

    error?.let { errMsg ->
        AlertDialog(
            onDismissRequest = { attendanceViewModel.resetState() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Check-In Failed", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = errMsg,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { attendanceViewModel.resetState() }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    onScanAreaMeasured: (Rect) -> Unit
) {
    val strokeColor = MaterialTheme.colorScheme.primary

    val infiniteTransition = rememberInfiniteTransition()
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScanLine"
    )

    Canvas(modifier = modifier) {
        val scanAreaSize = 260.dp.toPx()
        val left = (size.width - scanAreaSize) / 2
        val top = (size.height - scanAreaSize) / 2
        val rect = Rect(left, top, left + scanAreaSize, top + scanAreaSize)

        onScanAreaMeasured(rect)

        // Draw darkened background with a hole
        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination: The whole screen darkened
            drawRect(Color.Black.copy(alpha = 0.65f))

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

        // Animated Scanning Line
        val lineY = rect.top + (rect.height * scanLineY)
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, strokeColor, Color.Transparent),
                startX = rect.left,
                endX = rect.right
            ),
            start = Offset(rect.left, lineY),
            end = Offset(rect.right, lineY),
            strokeWidth = 3.dp.toPx()
        )
        // Soft glow behind the scan line
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, strokeColor.copy(alpha = 0.3f), Color.Transparent),
                startX = rect.left,
                endX = rect.right
            ),
            start = Offset(rect.left, lineY),
            end = Offset(rect.right, lineY),
            strokeWidth = 12.dp.toPx()
        )

        // Draw Corner Brackets (Perfectly Aligned)
        val bracketThickness = 6.dp.toPx()
        val bracketLength = 40.dp.toPx()
        val cornerRadius = 24.dp.toPx()

        // Offset path so the stroke sits completely outside the transparent hole
        val offset = bracketThickness / 2f
        val outRect = Rect(
            left = rect.left - offset,
            top = rect.top - offset,
            right = rect.right + offset,
            bottom = rect.bottom + offset
        )
        val outRadius = cornerRadius + offset

        // Top Left
        drawPath(
            path = Path().apply {
                moveTo(outRect.left, outRect.top + bracketLength)
                lineTo(outRect.left, outRect.top + outRadius)
                arcTo(
                    rect = Rect(outRect.left, outRect.top, outRect.left + outRadius * 2, outRect.top + outRadius * 2),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(outRect.left + bracketLength, outRect.top)
            },
            color = strokeColor,
            style = Stroke(width = bracketThickness, cap = StrokeCap.Round)
        )

        // Top Right
        drawPath(
            path = Path().apply {
                moveTo(outRect.right - bracketLength, outRect.top)
                lineTo(outRect.right - outRadius, outRect.top)
                arcTo(
                    rect = Rect(outRect.right - outRadius * 2, outRect.top, outRect.right, outRect.top + outRadius * 2),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(outRect.right, outRect.top + bracketLength)
            },
            color = strokeColor,
            style = Stroke(width = bracketThickness, cap = StrokeCap.Round)
        )

        // Bottom Left
        drawPath(
            path = Path().apply {
                moveTo(outRect.left, outRect.bottom - bracketLength)
                lineTo(outRect.left, outRect.bottom - outRadius)
                arcTo(
                    rect = Rect(outRect.left, outRect.bottom - outRadius * 2, outRect.left + outRadius * 2, outRect.bottom),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -90f,
                    forceMoveTo = false
                )
                lineTo(outRect.left + bracketLength, outRect.bottom)
            },
            color = strokeColor,
            style = Stroke(width = bracketThickness, cap = StrokeCap.Round)
        )

        // Bottom Right
        drawPath(
            path = Path().apply {
                moveTo(outRect.right - bracketLength, outRect.bottom)
                lineTo(outRect.right - outRadius, outRect.bottom)
                arcTo(
                    rect = Rect(outRect.right - outRadius * 2, outRect.bottom - outRadius * 2, outRect.right, outRect.bottom),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = -90f,
                    forceMoveTo = false
                )
                lineTo(outRect.right, outRect.bottom - bracketLength)
            },
            color = strokeColor,
            style = Stroke(width = bracketThickness, cap = StrokeCap.Round)
        )
    }
}
