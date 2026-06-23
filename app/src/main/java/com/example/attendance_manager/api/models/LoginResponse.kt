package com.example.attendance_manager.api.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("status") val status: String?
)