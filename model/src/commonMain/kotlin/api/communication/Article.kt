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
class ArticleUpstream(
    override val content: NonBlankString = NonBlankString.default,
    override val anonymity: Boolean = true,

    val code: NonBlankString,
    val displayName: NonBlankString = NonBlankString.default,
    val termYear: NonBlankString = NonBlankString.default,
) : CommentableUpstream

@Immutable
@Serializable
class ArticleEditRequest(
    override val content: NonBlankString? = null,
    override val anonymity: Boolean? = null,

    val code: NonBlankString? = null,
    val displayName: NonBlankString? = null,
) : CommentableEditRequest

fun ArticleEditRequest.isEmpty(): Boolean {
    return content == null && anonymity == null && code == null && displayName == null
}

@Immutable
@Serializable
class ArticleDownstream(
    override val coid: Uuid,
    override val author: User?,
    override val content: String,
    override val anonymity: Boolean,
    override val likes: UInt,
    override val dislikes: UInt,

    val code: String,
    val displayName: String,
    val course: Course,
    val termYear: String,

    val questionIndexes: List<String>,
    val comments: List<Uuid>,
    val stars: UInt,
    val views: UInt,
) : CommentableDownstream
