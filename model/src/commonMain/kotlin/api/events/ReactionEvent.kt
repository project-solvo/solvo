@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model.api.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.api.communication.Reaction
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

@Serializable
sealed interface ReactionEvent : QuestionPageEvent

@Serializable
data class UpdateReactionEvent(
    val reaction: Reaction,
    override val parentCoid: Uuid,
    override val questionCoid: Uuid,
) : ReactionEvent
