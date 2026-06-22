package com.example.attendance_manager.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AttendEaseShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),  // sm: 0.25rem
    small = RoundedCornerShape(8.dp),       // DEFAULT: 0.5rem
    medium = RoundedCornerShape(12.dp),     // md: 0.75rem
    large = RoundedCornerShape(16.dp),      // lg: 1rem (Standard Cards)
    extraLarge = RoundedCornerShape(24.dp)  // xl: 1.5rem (Dashboard Feature Cards)
)

// Standalone shape for full rounded (pill-shaped / 9999px) requirements
val ShapeFull = CircleShape