@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model.api.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.api.communication.Reaction
import org.solvo.model.api.communication.ReactionKind
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

sealed interface ReactionEvent : QuestionPageEvent

@Serializable
data class UpdateReactionServerEvent(
    val reactionKind: ReactionKind,
    val userIds: List<Uuid>,
    override val parentCoid: Uuid,
    override val questionCoid: Uuid,
) : ReactionEvent {
    fun of(user: Uuid?) = UpdateReactionClientEvent(
        Reaction(
            reactionKind,
            userIds.size,
            user?.let { userIds.contains(it) } ?: false
        ),
        parentCoid,
        questionCoid,
    )
}

@Serializable
data class UpdateReactionClientEvent(
    val reaction: Reaction,
    override val parentCoid: Uuid,
    override val questionCoid: Uuid,
) : ReactionEvent