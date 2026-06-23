package com.example.attendease

import android.app.Application
import com.example.attendease.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AttendanceApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AttendanceApplication)
            modules(appModule)
        }
    }
}
