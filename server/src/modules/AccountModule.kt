package org.solvo.server.modules

import io.ktor.client.content.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.solvo.model.api.communication.ImageUrlExchange
import org.solvo.server.ServerContext
import java.util.*

fun Application.accountModule() {
    val accounts = ServerContext.Databases.accounts
    val resources = ServerContext.Databases.resources

    routeApi {
        route("/account") {
            authenticate("authBearer") {
                get("/me") {
                    val uid = getUserId() ?: return@get
                    val userInfo = accounts.getUserInfo(uid)!!
                    call.respond(userInfo)
                }
                post("/newAvatar") {
                    val uid = getUserId() ?: return@post
                    val contentType = call.request.contentType()
                    val path = call.receiveStream().use { input ->
                        resources.uploadNewAvatar(uid, input, contentType)
                    }
                    call.respond(ImageUrlExchange(path))
                }
            }
            get("search") {
                val name: String = call.parameters.getOrFail("name") // currently required
                val userInfo = accounts.searchUsers(name)
                call.respond(userInfo)
            }
            get("/{uid}/avatar") {
                val uid = UUID.fromString(call.parameters.getOrFail("uid"))
                val (avatar, contentType) = accounts.getUserAvatar(uid) ?: kotlin.run {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(LocalFileContent(avatar, contentType))
            }
        }
    }
}
