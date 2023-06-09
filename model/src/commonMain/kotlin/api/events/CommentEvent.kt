package org.solvo.model.api.events

import kotlinx.serialization.Serializable
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.foundation.Uuid

@Serializable
sealed interface CommentEvent : Event {
    val parentCoid: Uuid
    val commentCoid: Uuid
}

@Serializable
data class UpdateCommentEvent(
    val commentDownstream: CommentDownstream,
) : CommentEvent {
    override val parentCoid: Uuid get() = commentDownstream.parent
    override val commentCoid: Uuid get() = commentDownstream.coid
}