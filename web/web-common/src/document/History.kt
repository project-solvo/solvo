package org.solvo.web.document

import kotlinx.browser.window

object History {

    /**
     * Navigate to page relative to origin
     */
    @PublishedApi
    internal fun navigate(path: String) {
        if (path.startsWith("http")) {
            window.location.href = path
        } else {
            window.location.href = window.location.origin.removeSuffix("/") + "/${path.removePrefix("/")}"
        }
    }

    inline fun navigate(page: WebPagePaths.() -> String) {
        return navigate(WebPagePaths.run(page))
    }

    inline fun navigateNotNull(page: WebPagePaths.() -> String?) {
        WebPagePaths.run(page)?.let {
            return navigate(it)
        }
    }
}

