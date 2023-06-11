package org.solvo.web.comments

import org.solvo.model.api.communication.Reaction
import org.solvo.model.api.events.ReactionEvent
import org.solvo.model.api.events.UpdateReactionClientEvent
import org.solvo.model.api.events.UpdateReactionServerEvent
import org.solvo.web.utils.replacedOrAppend

class ReactionEventHandler(
    private val getAllReactions: () -> List<Reaction>,
) {
    fun handleEvent(event: ReactionEvent): List<Reaction> = when (event) {
        is UpdateReactionClientEvent -> getAllReactions().replacedOrAppend(
            { it.kind == event.reaction.kind },
            event.reaction
        )

        is UpdateReactionServerEvent -> error("Should not receive an UpdateReactionServerEvent")
    }
}