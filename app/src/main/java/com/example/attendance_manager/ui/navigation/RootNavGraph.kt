package com.example.attendance_manager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.attendance_manager.ui.navigation.admin.adminGraph
import com.example.attendance_manager.ui.navigation.auth.authGraph
import com.example.attendance_manager.ui.navigation.lecturer.lecturerGraph
import com.example.attendance_manager.ui.navigation.student.studentGraph

@Composable
fun RootNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = Screen.Login.route
    ) {

        authGraph(navController)

        adminGraph(navController)

        lecturerGraph(navController)

        studentGraph(navController)
    }
}