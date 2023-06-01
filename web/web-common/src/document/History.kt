package org.solvo.web.document

import kotlinx.browser.window
import org.solvo.web.document.parameters.WindowEvents
import org.w3c.dom.CustomEvent

object History {
    const val DEFAULT_TITLE = "Solvo"

    /**
     * Navigate to page relative to origin
     */
    @PublishedApi
    internal fun navigate(
        path: String
    ) {
        if (path.startsWith("http")) {
            window.location.href = path
        } else {
            window.location.href = window.location.origin.removeSuffix("/") + "/${path.removePrefix("/")}"
        }
    }

    @PublishedApi
    internal fun pushState(
        data: Any?,
        title: String = DEFAULT_TITLE,
        path: String,
    ) {
        window.history.pushState(data, title, path)
        window.dispatchEvent(CustomEvent(WindowEvents.EVENT_PUSH_STATE))
    }

    inline fun navigate(page: WebPagePaths.() -> String) {
        return navigate(WebPagePaths.run(page))
    }

    inline fun navigateNotNull(page: WebPagePaths.() -> String?) {
        WebPagePaths.run(page)?.let {
            return navigate(it)
        }
    }

    inline fun pushState(
        data: Any? = null,
        title: String = DEFAULT_TITLE,
        page: WebPagePaths.() -> String
    ) {
        return pushState(data, title, WebPagePaths.run(page))
    }

    inline fun pushState(
        data: Any? = null,
        title: String = DEFAULT_TITLE,
        page: WebPagePaths.() -> String?
    ) {
        WebPagePaths.run(page)?.let {
            return pushState(data, title, it)
        }
    }
}

