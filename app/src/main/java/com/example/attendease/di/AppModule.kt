package com.example.attendease.di

import com.example.attendease.data.api.AuthApi
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.repository.AuthRepository
import com.example.attendease.viewModel.AuthViewModel
import com.example.attendease.data.api.DepartmentApi
import com.example.attendease.data.api.LecturerApi
import com.example.attendease.data.api.StudentApi
import com.example.attendease.data.api.CourseApi
import com.example.attendease.data.api.AcademicSessionApi
import com.example.attendease.data.api.CourseAssignmentApi
import com.example.attendease.data.api.AttendanceSessionApi
import com.example.attendease.data.api.AttendanceApi
import com.example.attendease.data.repository.LecturerRepository
import com.example.attendease.data.repository.StudentRepository
import com.example.attendease.data.repository.CourseAssignmentRepository
import com.example.attendease.data.repository.AcademicSessionRepository
import com.example.attendease.data.repository.DepartmentRepository
import com.example.attendease.data.repository.CourseRepository
import com.example.attendease.data.repository.AttendanceSessionRepository
import com.example.attendease.data.repository.AttendanceRepository
import com.example.attendease.data.session.SessionManager
import com.example.attendease.viewModel.LecturerViewModel
import com.example.attendease.viewModel.StudentViewModel
import com.example.attendease.viewModel.CourseAssignmentViewModel
import com.example.attendease.viewModel.AcademicSessionViewModel
import com.example.attendease.viewModel.DepartmentViewModel
import com.example.attendease.viewModel.CourseViewModel
import com.example.attendease.viewModel.DashboardViewModel
import com.example.attendease.data.api.DashboardApi
import com.example.attendease.data.repository.DashboardRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.attendease.utils.ConnectivityObserver
import com.example.attendease.utils.NetworkConnectivityObserver
import com.example.attendease.data.api.UserApi
import com.example.attendease.data.repository.UserRepository
import com.example.attendease.viewModel.UserViewModel
import com.example.attendease.viewModel.LecturerSessionViewModel
import com.example.attendease.viewModel.AttendanceViewModel



val appModule = module {
    single<ConnectivityObserver> { NetworkConnectivityObserver(get()) }
    single { SessionManager(get()) }
    single { NetworkClient.client }
    single { AuthApi(get(), get()) }
    single { AuthRepository(get(), get()) }
    single { DashboardApi(get(), get(), get()) }
    single { DashboardRepository(get()) }
    single { UserApi(get(), get(), get()) }

    single { LecturerApi(get(), get(), get()) }
    single { StudentApi(get(), get(), get()) }
    single { DepartmentApi(get(), get(), get()) }
    single { CourseApi(get(), get(), get()) }
    single { AcademicSessionApi(get(), get(), get()) }
    single { CourseAssignmentApi(get(), get(), get()) }
    single { AttendanceSessionApi(get(), get(), get()) }
    single { AttendanceApi(get(), get(), get()) }

    single { LecturerRepository(get(), get()) }
    single { StudentRepository(get(), get()) }
    single { CourseAssignmentRepository(get(), get(), get(), get()) }
    single { AcademicSessionRepository(get()) }
    single { DepartmentRepository(get()) }
    single { CourseRepository(get()) }
    single { UserRepository(get()) }
    single { AttendanceSessionRepository(get()) }
    single { AttendanceRepository(get()) }
    single { com.example.attendease.data.api.AttendanceWebSocketClient(get(), get()) }
    single { LecturerSessionViewModel(get(), get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { LecturerViewModel(get()) }
    viewModel { StudentViewModel(get()) }
    viewModel { CourseAssignmentViewModel(get()) }
    viewModel { AcademicSessionViewModel(get()) }
    viewModel { DepartmentViewModel(get()) }
    viewModel { CourseViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { AttendanceViewModel(get()) }
}
