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
import com.example.attendease.data.repository.LecturerRepository
import com.example.attendease.data.repository.StudentRepository
import com.example.attendease.data.repository.CourseAssignmentRepository
import com.example.attendease.data.repository.AcademicSessionRepository
import com.example.attendease.data.session.SessionManager
import com.example.attendease.viewModel.LecturerViewModel
import com.example.attendease.viewModel.StudentViewModel
import com.example.attendease.viewModel.CourseAssignmentViewModel
import com.example.attendease.viewModel.AcademicSessionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SessionManager(get()) }
    single { NetworkClient.client }
    single { AuthApi(get(), get()) }
    single { AuthRepository(get(), get()) }

    single { LecturerApi(get(), get(), get()) }
    single { StudentApi(get(), get(), get()) }
    single { DepartmentApi(get(), get(), get()) }
    single { CourseApi(get(), get(), get()) }
    single { AcademicSessionApi(get(), get(), get()) }
    single { CourseAssignmentApi(get(), get(), get()) }

    single { LecturerRepository(get(), get()) }
    single { StudentRepository(get(), get()) }
    single { CourseAssignmentRepository(get(), get(), get(), get()) }
    single { AcademicSessionRepository(get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { LecturerViewModel(get()) }
    viewModel { StudentViewModel(get()) }
    viewModel { CourseAssignmentViewModel(get()) }
    viewModel { AcademicSessionViewModel(get()) }
}
