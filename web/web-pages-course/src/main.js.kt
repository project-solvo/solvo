package org.solvo.web

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.solvo.model.api.WebPagePatterns
import org.solvo.web.document.History
import org.solvo.web.document.PathParameters
import org.solvo.web.document.WebPagePaths
import org.solvo.web.document.get
import org.solvo.web.requests.client

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    // Currently, jump to first article

    GlobalScope.launch {
        val code = PathParameters(WebPagePaths.courses())[WebPagePatterns.VAR_COURSE_CODE] ?: return@launch
        val article = client.courses.getAllArticles(code)?.firstOrNull() ?: return@launch
        History.navigate { article(code, article.code) }
    }
}