@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model.api.events

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer


@Serializable
sealed interface CommentEvent : QuestionPageEvent {
    val commentCoid: Uuid
}

@Serializable
data class UpdateCommentEvent(
    val commentDownstream: CommentDownstream,
    override val questionCoid: Uuid,
) : CommentEvent {
    override val parentCoid: Uuid get() = commentDownstream.parent
    override val commentCoid: Uuid get() = commentDownstream.coid
}

@Serializable
data class RemoveCommentEvent(
    val commentDownstream: CommentDownstream,
    override val questionCoid: @Contextual Uuid,
) : CommentEvent {
    override val parentCoid: Uuid get() = commentDownstream.parent
    override val commentCoid: Uuid get() = commentDownstream.coid
}
