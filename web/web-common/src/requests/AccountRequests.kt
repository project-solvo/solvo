package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.solvo.model.api.communication.*
import org.solvo.model.utils.NonBlankString
import org.w3c.files.File

class AccountRequests(
    override val client: Client,
) : Requests() {

    /**
     * Get self info. Returns `null` if not logged in or token outdated.
     */
    suspend fun getSelf(): User? {
        return try {
            val resp = http.get(api("account/me")) {
                accountAuthorization()
            }
            if (!resp.status.isSuccess()) return null
            resp.body<User>()
        } catch (e: NotAuthorizedException) {
            null
        }
    }

    suspend fun searchUsers(username: String): List<User> {
        return http.get(api("account/search?name=$username")).body<List<User>>()
    }

    suspend fun authenticate(username: String, password: String, isRegister: Boolean): AuthResponse {
        val path = if (isRegister) "${apiUrl}/register" else "${apiUrl}/login"
        val resp = http.post(path) {
            contentType(ContentType.Application.Json)
            setBody(
                AuthRequest(
                    username = NonBlankString.fromString(username),
                    password = NonBlankString.fromString(password),
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

    suspend fun uploadAvatar(data: File): ImageUrlExchange {
        return http.postAuthorized(api("account/newAvatar")) {
            contentType(ContentType.Image.Any)
            setBodyFile(data)
        }.body<ImageUrlExchange>()
    }
}
