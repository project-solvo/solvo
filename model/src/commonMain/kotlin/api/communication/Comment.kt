@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model.api.communication

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.annotations.Immutable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer
import org.solvo.model.utils.NonBlankString


@Immutable
@Serializable
class CommentUpstream(
    override val content: NonBlankString,
    override val anonymity: Boolean = false,
) : CommentableUpstream

@Immutable
@Serializable
class CommentEditRequest(
    val content: NonBlankString? = null,
    val anonymity: Boolean? = null,
)

fun CommentEditRequest.isEmpty(): Boolean {
    return content == null && anonymity == null
}

@Immutable
@Serializable
data class CommentDownstream(
    override val coid: Uuid,
    override val author: User?,
    override val content: String,
    override val anonymity: Boolean,
    override val likes: UInt,
    override val dislikes: UInt,

    val parent: Uuid,
    val pinned: Boolean,
    val postTime: Long,
    val lastEditTime: Long,
    val lastCommentTime: Long,
    val previewSubComments: List<LightCommentDownstream>, // up to 3
    val allSubCommentIds: List<Uuid>,

    val answerCode: Int? = null,
    val kind: CommentKind,
    val isSelf: Boolean,
) : CommentableDownstream, ICommentDownstream

@Immutable
@Serializable
class LightCommentDownstream(
    override val author: User?,
    override val content: String,
) : ICommentDownstream

sealed interface ICommentDownstream {
    val author: User?
    val content: String
}
