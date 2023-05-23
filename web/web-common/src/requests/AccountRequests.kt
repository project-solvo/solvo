package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.solvo.model.api.AuthRequest
import org.solvo.model.api.AuthResponse

class AccountRequests(
    override val client: Client,
) : Requests {
    suspend fun authenticate(username: String, password: String, isRegister: Boolean): AuthResponse {
        val path = if (isRegister) "${origin}/register" else "${origin}/login"
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
}
