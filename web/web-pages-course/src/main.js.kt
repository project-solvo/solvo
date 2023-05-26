package org.solvo.web

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.web.document.History
import org.solvo.web.document.WebPagePaths
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.document.parameters.get
import org.solvo.web.requests.client

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    // Currently, jump to first article

    GlobalScope.launch {
        val code = PathParameters(WebPagePaths.courses())[WebPagePathPatterns.VAR_COURSE_CODE] ?: return@launch
        val article = client.courses.getAllArticles(code)?.firstOrNull() ?: return@launch
        History.navigate { article(code, article.code) }
    }
}