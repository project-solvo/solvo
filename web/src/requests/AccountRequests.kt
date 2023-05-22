package org.solvo.web.requests

import io.ktor.client.request.*
import org.solvo.model.AccountChecker
import org.solvo.model.RegisterReqeust

class AccountRequests(
    override val client: Client,
) : Requests {
    suspend fun register(username: String, password: String) {
        http.post("${origin}/register") {
            setBody(
                RegisterReqeust(
                    username = username,
                    password = AccountChecker.hashPassword(password)
                )
            )
        }
    }
}
