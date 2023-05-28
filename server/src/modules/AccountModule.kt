package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.solvo.model.api.UploadImageResponse
import org.solvo.server.ServerContext
import java.util.*

fun Application.accountModule() {
    val accounts = ServerContext.Databases.accounts
    val contents = ServerContext.Databases.contents

    routeApi {
        authenticate("authBearer") {
            route("/account/{uid}") {
                put("/newAvatar") {
                    val uid = matchUserId(call.parameters["uid"]) ?: return@put
                    val input = call.receiveStream()

                    val path = accounts.uploadNewAvatar(uid, input, contents)
                    call.respond(UploadImageResponse(path))
                }
                get("/avatar") {
                    val uid = UUID.fromString(call.parameters["uid"])
                    val avatar = accounts.getUserAvatar(uid)
                    if (avatar == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        call.respondFile(avatar)
                    }
                }
            }
        }
    }
}
