package org.solvo.web.requests

import kotlinx.browser.window

interface Requests {
    val client: Client
}

@Suppress("UnusedReceiverParameter")
val Requests.origin get() = window.location.origin

val Requests.http get() = client.http
