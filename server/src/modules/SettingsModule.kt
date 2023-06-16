package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.pipeline.*
import org.solvo.model.api.communication.AdminSettings
import org.solvo.model.foundation.Uuid
import org.solvo.model.utils.UserPermission
import org.solvo.server.ServerContext
import org.solvo.server.database.AccountDBFacade
import java.util.*

fun Application.settingsModule() {
    val accounts = ServerContext.Databases.accounts
    routeApi {
        authenticate("authBearer") {
            route("/settings") {
                get("/admin") {
                    if (!checkIsAdmin(accounts)) return@get

                    call.respond(
                        AdminSettings(
                            operators = accounts.getOperators()
                        )
                    )
                }
                post("/admins/{targetId}") {
                    if (!checkIsAdmin(accounts)) return@post
                    val targetId: String by call.parameters
                    call.respond(
                        accounts.setOperator(Uuid.fromString(targetId))
                    )
                }
                delete("/admins/{targetId}") {
                    if (!checkIsAdmin(accounts)) return@delete
                    val targetId: String by call.parameters
                    call.respond(
                        accounts.removeOperator(Uuid.fromString(targetId))
                    )
                }
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.checkIsAdmin(
    accounts: AccountDBFacade
): Boolean {
    val uid = call.principal<UserIdPrincipal>()?.let { UUID.fromString(it.name) } ?: kotlin.run {
        call.respond(HttpStatusCode.Unauthorized)
        return false
    }
    if (accounts.getUserInfo(uid)?.permission != UserPermission.ROOT) {
        call.respond(HttpStatusCode.Unauthorized)
        return false
    }
    return true
}
