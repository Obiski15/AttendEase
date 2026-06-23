package com.example.attendease.di

import com.example.attendease.data.api.AuthApi
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.repository.AuthRepository
import com.example.attendease.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

import com.example.attendease.data.session.SessionManager

val appModule = module {
    single { SessionManager(get()) }
    single { NetworkClient.client }
    single { AuthApi(get(), get()) }
    single { AuthRepository(get(), get()) }
    viewModel { AuthViewModel(get()) }
}
