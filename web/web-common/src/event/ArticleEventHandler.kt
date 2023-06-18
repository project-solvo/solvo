package org.solvo.web.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.events.ArticleEvent
import org.solvo.model.api.events.RemoveArticleEvent
import org.solvo.model.api.events.UpdateArticleEvent

fun StateFlow<ArticleDownstream?>.withEvents(events: Flow<ArticleEvent>): Flow<ArticleDownstream?> {
    val handler = ArticleEventHandler { this.value }
    val eventMapped = events.map { handler.handleEvent(it) }
    return merge(this, eventMapped)
}

class ArticleEventHandler(
    private val getCurrentArticle: () -> ArticleDownstream?,
) {
    fun handleEvent(event: ArticleEvent): ArticleDownstream? {
        when (event) {
            is RemoveArticleEvent -> {
                val current = getCurrentArticle() ?: return null
                if (current.coid == event.articleCoid) {
                    return null
                }
                return current
            }

            is UpdateArticleEvent -> {
                val current = getCurrentArticle() ?: return null
                if (current.coid == event.articleCoid) return event.articleDownstream
                return current
            }
        }
    }
}