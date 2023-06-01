package org.solvo.server.modules

import io.ktor.client.content.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.solvo.model.api.ImageUrlExchange
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
                    val input = call.receiveStream()

                    val path = resources.uploadNewAvatar(uid, input, contentType)
                    call.respond(ImageUrlExchange(path))
                }
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
