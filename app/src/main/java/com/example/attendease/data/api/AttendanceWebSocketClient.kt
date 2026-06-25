package com.example.attendease.data.api

import com.example.attendease.BuildConfig
import com.example.attendease.data.network.NetworkClient
import com.example.attendease.data.session.SessionManager
import com.example.attendease.dto.response.AttendanceRecordResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class AttendanceWebSocketClient(
    private val client: HttpClient = NetworkClient.client,
    private val sessionManager: SessionManager
) {

    private val json = Json { ignoreUnknownKeys = true }

    fun observeAttendance(sessionId: String): Flow<AttendanceRecordResponse> = flow {
        val token = sessionManager.getAccessToken() ?: throw Exception("Unauthorized")
        
        // Convert http:// to ws:// and https:// to wss://
        val wsUrl = BuildConfig.BASE_URL.replace("http://", "ws://").replace("https://", "wss://")
        val path = "$wsUrl/ws/attendance/$sessionId?token=$token"

        client.webSocket(path) {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    try {
                        val record = json.decodeFromString<AttendanceRecordResponse>(text)
                        emit(record)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
