package com.example.attendease.ui.screens.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.attendease.data.session.SessionManager
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.navigation.Screen
import com.example.attendease.utils.BiometricHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    navController: NavController,
    sessionManager: SessionManager = koinInject()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val maxRadius = kotlin.math.hypot(screenWidthPx / 2, screenHeightPx / 2)

    val secondaryColor = MaterialTheme.colorScheme.secondary

    // Animation States
    val dotY = remember { Animatable(-screenHeightPx / 2f) }
    val dotAlpha = remember { Animatable(1f) }
    
    val ringRadius = remember { Animatable(0f) }
    val ringAlpha = remember { Animatable(1f) }
    
    val textScale = remember { Animatable(0.1f) }
    val textAlpha = remember { Animatable(0f) }
    
    val shimmerTranslate = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(400) // Brief pause at start to build anticipation

        // Phase 1: The Drop
        dotY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 400, easing = FastOutLinearInEasing)
        )

        // Phase 2: The Shockwave
        // Run dot hide, ring expansion, and text blast concurrently
        launch { dotAlpha.animateTo(0f, tween(50)) }
        
        launch {
            ringRadius.animateTo(maxRadius, tween(durationMillis = 700, easing = LinearOutSlowInEasing))
        }
        launch {
            ringAlpha.animateTo(0f, tween(durationMillis = 700, easing = LinearOutSlowInEasing))
        }

        // Phase 3: The Cinematic Reveal (Text blasts out)
        launch {
            textAlpha.animateTo(1f, tween(durationMillis = 300, easing = FastOutSlowInEasing))
        }
        textScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.45f, // Very Bouncy
                stiffness = Spring.StiffnessLow
            )
        )

        // Phase 4: The Shimmer Sweep
        shimmerTranslate.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )

        delay(800) // Hold for effect before transitioning

        val isLoggedIn = sessionManager.isLoggedIn()

        // Route to the correct destination
        val nextDestination = if (isLoggedIn) {
            when (sessionManager.getUserRole()) {
                UserRole.STUDENT -> Screen.StudentDashboard.route
                UserRole.LECTURER -> Screen.LecturerDashboard.route
                UserRole.ADMIN -> Screen.AdminDashboard.route
                null -> Screen.Login.route
            }
        } else {
            Screen.Login.route
        }

        navController.navigate(nextDestination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    val bgColor = MaterialTheme.colorScheme.background
    val onBgColor = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        // Canvas for Dot and Shockwave
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)

            // Draw Shockwave Ring
            if (ringRadius.value > 0f) {
                drawCircle(
                    color = secondaryColor.copy(alpha = ringAlpha.value),
                    radius = ringRadius.value,
                    center = center,
                    style = Stroke(width = 12.dp.toPx())
                )
            }

            // Draw Dropping Dot
            if (dotAlpha.value > 0f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(onBgColor, secondaryColor),
                        center = center.copy(y = center.y + dotY.value),
                        radius = 24.dp.toPx()
                    ),
                    radius = 16.dp.toPx(),
                    center = center.copy(y = center.y + dotY.value),
                    alpha = dotAlpha.value
                )
            }
        }

        // Text with Shimmer Effect
        if (textAlpha.value > 0f) {
            val shimmerBrush = Brush.linearGradient(
                colors = listOf(
                    onBgColor,
                    secondaryColor,
                    onBgColor
                ),
                start = Offset(x = (shimmerTranslate.value * screenWidthPx * 2) - screenWidthPx, y = 0f),
                end = Offset(x = (shimmerTranslate.value * screenWidthPx * 2) - screenWidthPx + 300f, y = 300f),
                tileMode = TileMode.Clamp
            )

            Text(
                text = "AttendEase",
                style = TextStyle(
                    brush = shimmerBrush,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = textScale.value
                        scaleY = textScale.value
                        alpha = textAlpha.value
                    }
            )
        }
    }
}
