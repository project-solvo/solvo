package org.solvo.server.modules

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cachingheaders.*
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
            cacheControl {
                listOf(CacheControl.MaxAge(maxAgeSeconds = 64000))
            }
        }
        routeStatic("/skiko.js", "/skiko.js") {
            call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 64000))
        }
        routeStatic("/skiko.wasm", "/skiko.wasm") {
            call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 64000))
        }

        // WEB PAGES
        routeWebPage(WebPagePathPatterns.home, "/index")
        routeWebPage(WebPagePathPatterns.auth, "/auth")
        routeWebPage(WebPagePathPatterns.course, "/course")
        routeWebPage(WebPagePathPatterns.article, "/article")
        routeWebPage(WebPagePathPatterns.question, "/question")
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
private fun Routing.routeWebPage(path: String, webPagePath: String) {
    routeStatic(path, "$webPagePath.html") {
        call.caching = CachingOptions(CacheControl.NoCache(null))
    }
    routeStatic("$webPagePath.js", "$webPagePath.js") {
        call.caching = CachingOptions(CacheControl.NoCache(null))
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