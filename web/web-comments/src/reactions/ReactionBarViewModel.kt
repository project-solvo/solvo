package org.solvo.web.comments.reactions

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.*
import org.solvo.model.foundation.Uuid
import org.solvo.web.requests.client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.solvo.model.api.communication.Reaction
import org.solvo.model.api.communication.ReactionKind
import org.solvo.web.utils.replacedOrPlus
import org.solvo.web.viewModel.AbstractViewModel

class ReactionBarViewModel(
    private val subjectCoid: Uuid,
    reactions: Flow<List<Reaction>>,
    private val applyLocalChange: (List<Reaction>) -> Unit,
) : AbstractViewModel() {
    private val reactions: StateFlow<List<Reaction>> =
        reactions.stateIn(backgroundScope, started = SharingStarted.Eagerly, emptyList())

    val reactionListOpen = mutableStateOf(false)

    @Stable
    fun reaction(kind: ReactionKind): Flow<Reaction?> = reactions.map { list -> list.find { it.kind == kind } }

    fun switchReactionList() {
        reactionListOpen.value = !reactionListOpen.value
    }

    private fun closeReactionList() {
        reactionListOpen.value = false
    }

    suspend fun react(kind: ReactionKind) {
        println("Sending reaction: $kind")
        val reactions = reactions.value
        val reaction = reactions.find { it.kind == kind } ?: Reaction(kind, 0, false)
        if (reaction.self) {
            applyLocalChange(
                reactions.replacedOrPlus({ it.kind == kind }, Reaction(kind, reaction.count - 1, false))
            )
            client.comments.removeReaction(subjectCoid, kind)
        } else {
            applyLocalChange(
                reactions.replacedOrPlus({ it.kind == kind }, Reaction(kind, reaction.count + 1, true))
            )
            client.comments.addReaction(subjectCoid, kind)
        }
        closeReactionList()
    }

}
