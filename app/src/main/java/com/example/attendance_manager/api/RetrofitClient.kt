package com.example.attendance_manager.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://web.com/api/login"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val attendanceApiService: AttendanceApiService by lazy {
        retrofit.create(AttendanceApiService::class.java)
    }
}