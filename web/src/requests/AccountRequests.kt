package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import org.solvo.model.AccountChecker
import org.solvo.model.AuthRequest
import org.solvo.model.AuthResponse

class AccountRequests(
    override val client: Client,
) : Requests {
    suspend fun register(username: String, password: String): AuthResponse {
        val resp = http.post("${origin}/register") {
            setBody(
                AuthRequest(
                    username = username,
                    hash = AccountChecker.hashPassword(password),
                )
            )
        }.body<AuthResponse>()
        return resp
    }
}
