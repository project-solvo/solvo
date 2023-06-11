package org.solvo.server.modules.content

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.solvo.server.ServerContext
import org.solvo.server.modules.routeApi
import java.io.File

fun Application.contentModule() {
    val contents = ServerContext.Databases.contents
    val accounts = ServerContext.Databases.accounts
    val resources = ServerContext.Databases.resources
    val events = ServerContext.events

    routing {
        staticFiles("/resources", File(ServerContext.paths.resourcesPath()), index = null) {
            // contentType { resources.getContentType(it) }
            preCompressed(CompressedFileType.GZIP, CompressedFileType.BROTLI)
            cacheControl {
                listOf(CacheControl.MaxAge(64000, visibility = CacheControl.Visibility.Public))
            }
        }
    }

    routeApi {
        imageRouting(resources)
        courseRouting(contents, accounts)
        sharedContentRouting(contents)
        commentRouting(contents, events)
        eventRouting(contents, events)
    }
}
