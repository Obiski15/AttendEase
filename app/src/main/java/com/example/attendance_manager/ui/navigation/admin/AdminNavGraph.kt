package com.example.attendance_manager.ui.navigation.admin


import com.example.attendance_manager.ui.navigation.Screen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import com.example.attendance_manager.ui.screens.admin.*


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
        route = Screen.StudentDetail.route,
        // Define the exact arguments this route expects
        arguments = listOf(
            navArgument("studentId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->

        // Extract the argument from the backStackEntry
        val studentId = backStackEntry.arguments?.getString("studentId")

        // Pass the extracted ID down to actual UI screen
        if (studentId != null) {
            StudentDetailScreen(navController = navController, studentId = studentId)
        }
    }

    composable(Screen.Courses.route) {
        CoursesScreen(navController)
    }

    composable(Screen.AddCourse.route){
        AddCourseScreen(navController)
    }

    composable(Screen.CourseAssignment.route) {
        CourseAssignmentScreen(navController)
    }

    composable(Screen.Lecturers.route) {
        LecturersScreen(navController)
    }

    composable(Screen.AddLecturer.route) {
        AddLecturerScreen(navController)
    }
}
