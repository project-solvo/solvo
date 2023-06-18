@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model.api.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

sealed interface QuestionEvent : ArticleSettingPageEvent, QuestionPageEvent

@Serializable
class UpdateQuestionEvent(
    val question: QuestionDownstream,
    val parent: ArticleDownstream,
) : QuestionEvent {
    override val articleCoid: Uuid
        get() = question.article
    override val questionCoid: Uuid
        get() = question.coid
    override val parentCoid: Uuid
        get() = articleCoid
}

@Serializable
class RemoveQuestionEvent(
    override val articleCoid: Uuid,
    override val questionCoid: Uuid
) : QuestionEvent {
    override val parentCoid: Uuid
        get() = articleCoid
}
