package org.solvo.web.document

import io.ktor.http.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.js.Date
import kotlin.time.Duration

object Cookies {
//    fun setCookie(cookie: Cookie) {
//        document.cookie = renderSetCookieHeader(cookie)
//    }

    fun setCookie(name: String, value: String, expires: Duration? = null, path: String? = window.location.origin) {
        document.cookie = buildString {
            append(name.encodeURLPathPart())
            append("=")
            append(value)
            if (expires != null) {
                val expireDate = Date()
                expireDate.asDynamic().setTime(expireDate.getTime() + expires.inWholeMilliseconds)
                append(";expires=${expireDate.toUTCString()}")
            }
            if (path != null) {
                append(";path=$path")
            }
        }
    }

    fun getCookie(name: String): String? {
        return document.cookie.splitToSequence(";")
            .find { it.substringBefore("=") == name }
            ?.substringAfter("=")
            ?.let { decodeCookieValue(it, CookieEncoding.URI_ENCODING) }
    }
}