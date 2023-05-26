package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.solvo.model.api.AuthRequest
import org.solvo.model.api.AuthResponse
import org.solvo.model.api.UsernameValidityResponse

class AccountRequests(
    override val client: Client,
) : Requests {
    suspend fun authenticate(username: String, password: String, isRegister: Boolean): AuthResponse {
        val path = if (isRegister) "${apiUrl}/register" else "${apiUrl}/login"
        val resp = http.post(path) {
            contentType(ContentType.Application.Json)
            setBody(
                AuthRequest(
                    username = username,
                    password = password,
                )
            )
        }.body<AuthResponse>()
        return resp
    }

    suspend fun checkUsername(username: String): UsernameValidityResponse {
        val path = "${apiUrl}/register/${username}"
        val resp = http.get(path) {
            contentType(ContentType.Application.Json)
        }.body<UsernameValidityResponse>()
        return resp
    }
}
