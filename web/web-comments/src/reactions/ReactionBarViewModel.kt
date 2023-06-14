package org.solvo.web.comments.reactions

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.solvo.model.api.communication.Reaction
import org.solvo.model.api.communication.ReactionKind
import org.solvo.model.api.events.Event
import org.solvo.model.api.events.ReactionEvent
import org.solvo.model.foundation.Uuid
import org.solvo.web.comments.ReactionEventHandler
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel
import kotlin.time.Duration.Companion.seconds

class ReactionBarViewModel(
    subjectCoidFlow: Flow<Uuid>,
    events: Flow<Event>,
) : AbstractViewModel() {
    private val subjectCoid = subjectCoidFlow.stateInBackground()

    private val eventHandler = ReactionEventHandler(
        getAllReactions = { allReactions.value }
    )

    private val eventReactions = events
        .filterIsInstance<ReactionEvent>()
        .filter { it.parentCoid == this.subjectCoid.value }
        .map {
            eventHandler.handleEvent(it)
        }

    val allReactions: StateFlow<List<Reaction>> = merge(
        eventReactions,
        subjectCoid.filterNotNull()
            .mapNotNull { client.comments.getReactions(it) }
    ).stateInBackground(emptyList())

    val reactionListOpen = mutableStateOf(false)
    val isEmpty = allReactions.map { list -> list.sumOf { it.count } == 0 }.shareInBackground()

    private val activeReacts =
        MutableSharedFlow<ReactionKind>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val showHintLineFlow = activeReacts.flatMapLatest {
        flow {
            emit(true)
            delay(6.seconds)
            emit(false)
        }
    }
    private val showHintLineOverride =
        MutableSharedFlow<Boolean>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val showHintLine = merge(showHintLineFlow, showHintLineOverride)

    @Stable
    fun reaction(kind: ReactionKind): StateFlow<Reaction> {
        val default = Reaction(kind, 0, false)
        return allReactions
            .map { list -> list.find { it.kind == kind } ?: default }
            .stateInBackground(default)
    }

    fun switchReactionList() {
        showHintLineOverride.tryEmit(false)
        reactionListOpen.value = !reactionListOpen.value
    }

    private fun closeReactionList() {
        reactionListOpen.value = false
    }

    suspend fun react(kind: ReactionKind) {
        val subjectCoid = subjectCoid.value ?: return
        val reactions = allReactions.value
        val reaction = reactions.find { it.kind == kind } ?: Reaction(kind, 0, false)
        if (reaction.isSelf) {
            client.comments.removeReaction(subjectCoid, kind)
        } else {
            activeReacts.emit(kind)
            client.comments.addReaction(subjectCoid, kind)
        }
        closeReactionList()
    }

}
