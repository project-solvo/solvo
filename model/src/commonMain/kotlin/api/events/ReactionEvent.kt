package org.solvo.model.api.events

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.solvo.model.api.communication.Reaction
import org.solvo.model.foundation.Uuid

@Serializable
sealed interface ReactionEvent : QuestionPageEvent

@Serializable
data class UpdateReactionEvent(
    val reaction: Reaction,
    override val parentCoid: @Contextual Uuid,
    override val questionCoid: @Contextual Uuid,
) : ReactionEvent
