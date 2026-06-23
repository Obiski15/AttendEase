package com.example.attendance_manager.api

import com.example.attendance_manager.api.models.LoginRequest
import com.example.attendance_manager.api.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AttendanceApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}