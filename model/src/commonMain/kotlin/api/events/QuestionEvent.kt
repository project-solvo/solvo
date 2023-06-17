package org.solvo.model.api.events

import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.foundation.Uuid

interface QuestionEvent : ArticleSettingPageEvent, QuestionPageEvent

class UpdateQuestionEvent(
    val questionDownstream: QuestionDownstream
) : QuestionEvent {
    override val articleCoid: Uuid
        get() = questionDownstream.article
    override val questionCoid: Uuid
        get() = questionDownstream.coid
    override val parentCoid: Uuid
        get() = articleCoid
}

class RemoveQuestionEvent(
    override val articleCoid: Uuid,
    override val questionCoid: Uuid
) : QuestionEvent {
    override val parentCoid: Uuid
        get() = articleCoid
}
