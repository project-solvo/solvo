@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model.api.communication

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.annotations.Immutable
import org.solvo.model.annotations.Stable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer
import org.solvo.model.utils.NonBlankString

@Immutable
@Serializable
class QuestionUpstream(
    override val content: NonBlankString = NonBlankString.defaultContent,
    override val anonymity: Boolean = true,

    val sharedContent: Uuid? = null,
) : CommentableUpstream

@Immutable
@Serializable
class QuestionEditRequest(
    override val content: NonBlankString? = null,
    override val anonymity: Boolean? = null,

    val code: NonBlankString? = null,
) : CommentableEditRequest

@Stable
fun QuestionEditRequest.isEmpty(): Boolean {
    return content == null && anonymity == null && code == null
}

@Immutable
@Serializable
class QuestionDownstream(
    override val coid: Uuid,
    override val author: User?,
    override val content: String,
    override val anonymity: Boolean,
    override val likes: UInt,
    override val dislikes: UInt,

    val sharedContent: SharedContent,
    val code: String,
    val article: Uuid,
    val answers: List<Uuid>,
    val comments: List<Uuid>,
) : CommentableDownstream