package org.solvo.web.answer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.ReactionKind
import org.solvo.model.api.events.Event
import org.solvo.web.comments.reactions.ReactionBarViewModel
import org.solvo.web.comments.reactions.ReactionButtonItem
import org.solvo.web.comments.reactions.ReactionsIconButton
import org.solvo.web.comments.reactions.rememberReactionBarViewModel
import org.solvo.web.viewModel.launchInBackgroundAnimated

@Composable
fun AnswerCardReactions(
    item: CommentDownstream,
    events: SharedFlow<Event>,
    betterIdeaHint: @Composable (RowScope.(ReactionBarViewModel) -> Unit)? = null,
) {
    val reactionBarState = rememberReactionBarViewModel(item.coid, events)

    FlowRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReactionsIconButton(reactionBarState, Modifier.offset(x = (-12).dp))

        val viewModelState by rememberUpdatedState(reactionBarState)

        for (kind in ReactionKind.entries) {
            ReactionButtonItem(kind, viewModelState) { isProcessing ->
                viewModelState.launchInBackgroundAnimated(isProcessing) {
                    react(kind)
                }
            }
        }

        betterIdeaHint?.invoke(this, reactionBarState)
    }
}
