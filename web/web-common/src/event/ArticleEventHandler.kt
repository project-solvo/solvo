package org.solvo.web.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.events.ArticleEvent
import org.solvo.model.api.events.RemoveArticleEvent
import org.solvo.model.api.events.UpdateArticleEvent
import org.solvo.web.utils.replacedOrAppend

fun StateFlow<List<ArticleDownstream>>.withEvents(
    events: Flow<ArticleEvent>,
): Flow<List<ArticleDownstream>> {
    val eventMapped = events.map { event -> handleEvent(event) }
    return merge(this, eventMapped)
}

private fun StateFlow<List<ArticleDownstream>>.handleEvent(
    event: ArticleEvent
): List<ArticleDownstream> {
    return when (event) {
        is RemoveArticleEvent -> {
            value.filterNot { it.coid == event.articleCoid }
        }

        is UpdateArticleEvent -> {
            value.replacedOrAppend({ it.coid == event.articleCoid }, event.articleDownstream)
        }
    }
}

fun StateFlow<ArticleDownstream?>.withEvents(
    events: Flow<ArticleEvent>,
    deleted: Deleted,
): Flow<ArticleDownstream?> {
    val handler = ArticleEventHandler(deleted) { this.value }
    val eventMapped = events.map { handler.handleEvent(it) }
    return merge(this, eventMapped)
}

class ArticleEventHandler(
    private val deleted: Deleted,
    private val getCurrentArticle: () -> ArticleDownstream?,
) {
    fun handleEvent(event: ArticleEvent): ArticleDownstream? {
        when (event) {
            is RemoveArticleEvent -> {
                val current = getCurrentArticle() ?: return null
                if (current.coid == event.articleCoid) {
                    deleted.setDeleted()
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