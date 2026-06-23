package com.example.attendease.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendease.enums.UserRole
import com.example.attendease.ui.navigation.Screen

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun AttendEaseBottomBar(
    userRole: UserRole,
    currentRoute: String?,
    navController: NavController
) {
    val bottomNavItems = when (userRole) {
        UserRole.ADMIN -> listOf(
            NavigationItem("Dashboard", Icons.Default.Dashboard, Screen.AdminDashboard.route),
            NavigationItem("Students", Icons.Default.People, Screen.Students.route),
            NavigationItem("Lecturers", Icons.Default.Badge, Screen.Lecturers.route),
            NavigationItem("Settings", Icons.Default.Settings, Screen.Settings.createRoute("ADMIN", "Admin User", "admin@university.edu"))
        )
        UserRole.LECTURER -> listOf(
            NavigationItem("Home", Icons.Default.Home, Screen.LecturerDashboard.route),
            NavigationItem("History", Icons.Default.History, Screen.SessionHistory.route),
            NavigationItem("Profile", Icons.Default.Person, Screen.Settings.createRoute("LECTURER", "Dr. Pam Beesly", "pam.beesly@university.edu"))
        )
        UserRole.STUDENT -> listOf(
            NavigationItem("Home", Icons.Default.Home, Screen.StudentDashboard.route),
            NavigationItem("Scan", Icons.Default.QrCodeScanner, Screen.ScanAttendance.route),
            NavigationItem("History", Icons.Default.History, Screen.AttendanceHistory.route),
            NavigationItem("Profile", Icons.Default.Person, Screen.Settings.createRoute("STUDENT", "Michael Scott", "michael.scott@university.edu"))
        )
    }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute?.substringBefore('?') == item.route.substringBefore('?')
            NavigationBarItem(
                selected = isSelected,
                onClick = { if (!isSelected) navController.navigate(item.route)  },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}
