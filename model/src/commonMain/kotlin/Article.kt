@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.annotations.Immutable
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

@Immutable
@Serializable
class ArticleUpstream(
    override val content: String,
    override val anonymity: Boolean,

    val code: String,
    val displayName: String,
    val termYear: String,

    val questions: List<QuestionUpstream>,
) : CommentableUpstream

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
