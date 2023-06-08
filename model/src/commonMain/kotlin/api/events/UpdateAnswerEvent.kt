package org.solvo.model.api.events

import kotlinx.serialization.Serializable
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.foundation.Uuid

sealed interface CommentEvent {
    val questionCoid: Uuid
    val commentCoid: Uuid
}

@Serializable
class UpdateCommentEvent(
    val commentDownstream: CommentDownstream,
) : CommentEvent {
    override val questionCoid: Uuid get() = commentDownstream.parent
    override val commentCoid: Uuid get() = commentDownstream.coid
}