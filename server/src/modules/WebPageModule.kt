package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.server.ServerMain

// See docs/WebPages.md
fun Application.webPageModule() {
    routing {
        // STATIC RESOURCES
        staticResources("/", "static", index = null) {
            preCompressed(CompressedFileType.GZIP, CompressedFileType.BROTLI)
            enableAutoHeadResponse()
            cacheControl {
                listOf(CacheControl.MaxAge(maxAgeSeconds = 64000))
            }
        }
        routeStatic("/skiko.js", "/skiko.js") {
            call.response.cacheControl(CacheControl.MaxAge(maxAgeSeconds = 64000))
        }
        routeStatic("/skiko.wasm", "/skiko.wasm") {
            call.response.cacheControl(CacheControl.MaxAge(maxAgeSeconds = 64000))
        }

        // WEB PAGES
        with(WebPagePathPatterns) {
            routeWebPage(home, "/index")
            routeWebPage(auth.replace(VAR_AUTH_METHOD, VAR_AUTH_METHOD_REGISTER), "/auth")
            routeWebPage(auth.replace(VAR_AUTH_METHOD, VAR_AUTH_METHOD_LOGIN), "/auth")
            routeWebPage(settingsAdmin, "/settings-admin")
            routeWebPage(me, "/user")
            routeWebPage(course, "/course")
            routeWebPage(article, "/article")
            routeWebPage(question, "/question")
        }
    }
}

@KtorDsl
private inline fun Routing.routeStatic(
    path: String, respondPath: String,
    noinline mimeResolve: (String) -> ContentType = { ContentType.defaultForFileExtension(it) },
    crossinline options: PipelineContext<Unit, ApplicationCall>.() -> Unit = {},
) {
    get(path) {
        val resourcePath = """/$RESOURCES_WEB_GENERATED/${respondPath.removeSuffix("/")}"""
        options()
        call.respond(HttpStatusCode.OK, call.getStaticResource(resourcePath, mimeResolve))
    }
}

/**
 * Route `.html` and `.js`
 */
@KtorDsl
private fun Routing.routeWebPage(path: String, localPath: String) {
    routeStatic(path, "$localPath.html") {
        call.response.cacheControl(CacheControl.MaxAge(30, mustRevalidate = true, proxyRevalidate = true))
    }
    routeStatic("$localPath.js", "$localPath.js") {
        call.response.cacheControl(CacheControl.MaxAge(30, mustRevalidate = true, proxyRevalidate = true))
    }
}

private val staticResourceLock = Mutex()
private val staticResources = mutableMapOf<String, OutgoingContent>()
private suspend fun ApplicationCall.getStaticResource(
    path: String,
    mimeResolve: (String) -> ContentType = { ContentType.defaultForFileExtension(it) }
): OutgoingContent {
    staticResources[path]?.let { return it }

    staticResourceLock.withLock {
        return staticResources.getOrPut(path) {
            resolveResource(path, classLoader = ServerMain::class.java.classLoader, mimeResolve = mimeResolve)
                ?: throw IllegalStateException("Cannot find static resource '$path'")
        }
    }
}

@KtorDsl
fun Route.getAny(vararg paths: String, body: PipelineInterceptor<Unit, ApplicationCall>) {
    for (path in paths) {
        route(path, HttpMethod.Get) { handle(body) }
    }
}

private const val RESOURCES_WEB_GENERATED = "web-generated" 