package org.solvo.web.requests

import kotlinx.browser.window

interface Requests {
    val client: Client
}

@Suppress("UnusedReceiverParameter")
val Requests.origin get() = window.location.origin.removeSuffix("/")
val Requests.apiUrl get() = "$origin/api"

val Requests.http get() = client.http
