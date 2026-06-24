package com.example.attendease.ui.navigation.admin


import com.example.attendease.ui.navigation.Screen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import com.example.attendease.ui.screens.admin.*


fun NavGraphBuilder.adminGraph(
    navController: NavController
) {

    composable(Screen.AdminDashboard.route) {
        AdminDashboardScreen(navController)
    }

    composable(Screen.Students.route) {
        StudentsScreen(navController)
    }

    composable(Screen.AddStudent.route) {
        AddStudentScreen(navController)
    }

    composable(
        route = Screen.EditStudent.route,
        arguments = listOf(
            navArgument("studentId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val studentId = backStackEntry.arguments?.getString("studentId")
        if (studentId != null) {
            EditStudentScreen(navController, studentId)
        }
    }

    composable(
        route = Screen.StudentDetail.route,
        arguments = listOf(
            navArgument("userId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId")
        if (userId != null) {
            StudentDetailScreen(navController = navController, userId = userId)
        }
    }

    composable(Screen.Courses.route) {
        CoursesScreen(navController)
    }

    composable(Screen.AddCourse.route){
        AddCourseScreen(navController)
    }

    composable(
        route = Screen.EditCourse.route,
        arguments = listOf(
            navArgument("courseId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        if (courseId != null) {
            EditCourseScreen(navController, courseId)
        }
    }

    composable(Screen.CourseAssignment.route) {
        CourseAssignmentScreen(navController)
    }

    composable(Screen.AcademicSessions.route) {
        AcademicSessionsScreen(navController)
    }

    composable(Screen.Departments.route) {
        DepartmentsScreen(navController)
    }

    composable(Screen.Lecturers.route) {
        LecturersScreen(navController)
    }

    composable(
        route = Screen.LecturerDetail.route,
        arguments = listOf(
            navArgument("lecturerId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val lecturerId = backStackEntry.arguments?.getString("lecturerId")
        if (lecturerId != null) {
            LecturerDetailScreen(navController = navController, lecturerId = lecturerId)
        }
    }

    composable(Screen.AddLecturer.route) {
        AddLecturerScreen(navController)
    }

    composable(
        route = Screen.EditLecturer.route,
        arguments = listOf(
            navArgument("lecturerId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val lecturerId = backStackEntry.arguments?.getString("lecturerId")
        if (lecturerId != null) {
            EditLecturerScreen(navController, lecturerId)
        }
    }

    composable(Screen.ManageAdmins.route) {
        ManageAdminsScreen(navController)
    }

    composable(Screen.AddAdmin.route) {
        AddAdminScreen(navController)
    }

    composable(
        route = Screen.EditAdmin.route,
        arguments = listOf(
            navArgument("userId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId")
        if (userId != null) {
            EditAdminScreen(navController, userId)
        }
    }
}
