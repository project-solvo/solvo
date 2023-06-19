package org.solvo.web.answer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.ReactionKind
import org.solvo.model.api.events.Event
import org.solvo.web.DraftAnswerControlBarState
import org.solvo.web.DraftKind
import org.solvo.web.comments.reactions.ReactionButtonItem
import org.solvo.web.comments.reactions.ReactionsIconButton
import org.solvo.web.comments.reactions.rememberReactionBarViewModel
import org.solvo.web.ui.foundation.IconTextButton
import org.solvo.web.viewModel.launchInBackgroundAnimated

@Composable
fun AnswerCardReactions(
    item: CommentDownstream,
    controlBarState: DraftAnswerControlBarState,
    events: SharedFlow<Event>,
) {
    val reactionBarState = rememberReactionBarViewModel(item.coid, events)
    val controlBarStateUpdated by rememberUpdatedState(controlBarState)

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

        val showHint by reactionBarState.showHintLine.collectAsState(false)
        AnimatedVisibility(showHint, exit = fadeOut()) {
            Row(
                modifier = Modifier.padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.AutoAwesome, null, Modifier.padding(vertical = 2.dp))
                Text(
                    "Have a better idea?",
                    Modifier.padding(start = 8.dp),
                    softWrap = false,
                )
                IconTextButton(
                    icon = { Icon(DraftKind.Thought.icon, null) },
                    text = { Text("Just share it", softWrap = false) },
                    onClick = { controlBarStateUpdated.startDraft(DraftKind.Thought) },
                    Modifier.padding(start = 12.dp)
                )
            }
        }

    }
}
