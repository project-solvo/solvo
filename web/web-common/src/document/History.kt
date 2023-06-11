package org.solvo.web.document

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.browser.window
import org.solvo.web.document.parameters.WindowEvents
import org.w3c.dom.CustomEvent

object History {
    const val DEFAULT_TITLE = "Solvo"

    @PublishedApi
    internal val webPagePaths = object : WebPagePaths() {}

    private val _pageVersion: MutableState<Int> = mutableStateOf(0)
    val pageVersion get() = _pageVersion.value

    fun advancePageVersion() {
        _pageVersion.value++
    }

//
//    private val _currentState = mutableStateOf(0)
//    val currentState: Any? get() = window.history.state

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
            var value = window.location.origin.removeSuffix("/") + "/${path.removePrefix("/")}"
            value = value.removeSuffix("/")
            window.location.href = value
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
        return navigate(webPagePaths.run(page))
    }

    inline fun navigateNotNull(page: WebPagePaths.() -> String?) {
        webPagePaths.run(page)?.let {
            return navigate(it)
        }
    }


    fun refresh() {
        window.location.reload()
    }

    inline fun pushState(
        data: Any? = null,
        title: String = DEFAULT_TITLE,
        page: WebPagePaths.() -> String
    ) {
        return pushState(data, title, webPagePaths.run(page))
    }

    inline fun pushState(
        data: Any? = null,
        title: String = DEFAULT_TITLE,
        page: WebPagePaths.() -> String?
    ) {
        webPagePaths.run(page)?.let {
            return pushState(data, title, it)
        }
    }
}

