package org.solvo.web.comments.reactions

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.solvo.model.Reaction
import org.solvo.model.ReactionKind
import org.solvo.web.utils.replacedOrPlus

@Stable
class ReactionBarState(
    reactions: List<Reaction>,
) {
    private val reactions = MutableStateFlow(reactions)

    @Stable
    fun reaction(kind: ReactionKind): Flow<Reaction?> = reactions.map { list -> list.find { it.kind == kind } }

    fun switchReactionList() {
        reactionListOpen.value = !reactionListOpen.value
    }

    fun closeReactionList() {
        reactionListOpen.value = false
    }

    fun react(kind: ReactionKind) {
        val reactions = reactions.value
        val reaction = reactions.find { it.kind == kind } ?: Reaction(kind, 0, false)
        if (reaction.self) {
            this.reactions.value =
                reactions.replacedOrPlus({ it.kind == kind }, Reaction(kind, reaction.count - 1, false))
            // TODO: 2023/6/7 send backend request 
        } else {
            this.reactions.value =
                reactions.replacedOrPlus({ it.kind == kind }, Reaction(kind, reaction.count + 1, true))
            // TODO: 2023/6/7 send backend request 
        }
        closeReactionList()
    }

    val reactionListOpen = mutableStateOf(false)
}
