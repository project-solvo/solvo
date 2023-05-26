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
import org.solvo.model.api.WebPagePatterns
import org.solvo.server.ServerMain

// See docs/WebPages.md
fun Application.webPageModule() {
    routing {

        // STATIC RESOURCES
        staticResources("/", "static", index = null)
        routeStatic("/skiko.js", "/skiko.js")
        routeStatic("/skiko.wasm", "/skiko.wasm")

        // WEB PAGES
        routeWebPage(WebPagePatterns.home, "/index")
        routeWebPage(WebPagePatterns.auth, "/auth")
        routeWebPage(WebPagePatterns.course, "/course")
        routeWebPage(WebPagePatterns.article, "/article")
        routeWebPage(WebPagePatterns.question, "/article")
    }
}

@KtorDsl
private fun Routing.routeStatic(
    path: String, respondPath: String,
    mimeResolve: (String) -> ContentType = { ContentType.defaultForFileExtension(it) }
) {
    get(path) {
        val resourcePath = """/$RESOURCES_WEB_GENERATED/${respondPath.removeSuffix("/")}"""
        call.respond(HttpStatusCode.OK, call.getStaticResource(resourcePath, mimeResolve))
    }
}

/**
 * Route `.html` and `.js`
 */
@KtorDsl
private fun Routing.routeWebPage(path: String, webPagePath: String) {
    routeStatic(path, "$webPagePath.html")
    routeStatic("$webPagePath.js", "$webPagePath.js")
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