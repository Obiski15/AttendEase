package com.example.attendease.data.api

import com.example.attendease.data.session.SessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ApiException(val code: Int, message: String) : Exception(message)

abstract class BaseApi(
    @PublishedApi internal val client: HttpClient,
    @PublishedApi internal val sessionManager: SessionManager
) {
    @PublishedApi internal var authApiProvider: (() -> AuthApi)? = null

    suspend inline fun <reified R> authenticatedRequest(
        method: HttpMethod,
        url: String,
        body: Any? = null
    ): R {
        val token = sessionManager.getAccessToken()
        val makeRequest: suspend (String?) -> HttpResponse = { activeToken ->
            client.request(url) {
                this.method = method
                if (activeToken != null) {
                    header("Authorization", "Bearer $activeToken")
                }
                if (body != null) {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            }
        }

        var response = makeRequest(token)

        if (response.status.value == 401) {
            val newTokens = try {
                val provider = authApiProvider ?: throw IllegalStateException("authApiProvider not initialized")
                provider().refreshTokens()
            } catch (e: Exception) {
                sessionManager.clearSessionAndNotify()
                throw UnauthorizedException("Session expired")
            }

            response = makeRequest(newTokens.accessToken)

            if (response.status.value == 401) {
                sessionManager.clearSessionAndNotify()
                throw UnauthorizedException("Session expired")
            }
        }

        if (response.status.value in 200..299) {
            if (response.status.value == 204) {
                return Unit as R
            }
            return response.body()
        } else {
            handleErrorResponse(response)
        }
    }

    suspend fun handleErrorResponse(response: HttpResponse): Nothing {
        val errorText = try {
            response.bodyAsText()
        } catch (e: Exception) {
            ""
        }

        val errorMessage = try {
            val json = Json.parseToJsonElement(errorText).jsonObject
            json["message"]?.jsonPrimitive?.content
                ?: json["error"]?.jsonPrimitive?.content
                ?: json["detail"]?.jsonPrimitive?.content
                ?: "An error occurred (Status: ${response.status.value})"
        } catch (e: Exception) {
            if (errorText.isNotBlank()) errorText else "An error occurred (Status: ${response.status.value})"
        }

        throw ApiException(response.status.value, errorMessage)
    }
}
