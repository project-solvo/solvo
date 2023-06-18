@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model.api.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

sealed interface ArticleEvent : ArticleSettingPageEvent, CoursePageEvent

@Serializable
class UpdateArticleEvent(
    val articleDownstream: ArticleDownstream
): ArticleEvent {
    override val articleCoid: Uuid
        get() = articleDownstream.coid

    override val courseCode: String
        get() = articleDownstream.course.code.str
}

@Serializable
class RemoveArticleEvent(
    override val articleCoid: Uuid,
    override val courseCode: String,
): ArticleEvent
