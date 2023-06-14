package org.solvo.web.comments.reactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddReaction
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import org.solvo.model.api.communication.ReactionKind
import org.solvo.model.api.events.Event
import org.solvo.model.foundation.Uuid
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.theme.EMOJI_FONT
import org.solvo.web.viewModel.launchInBackgroundAnimated

@Composable
fun rememberReactionBarViewModel(
    subjectCoid: Uuid,
    events: Flow<Event>
): ReactionBarViewModel {
    val subjectCoidUpdated by rememberUpdatedState(subjectCoid)
    val flow = snapshotFlow { subjectCoidUpdated }
    return remember(events) {
        ReactionBarViewModel(flow, events)
    }
}

@Composable
fun ReactionBar(
    viewModel: ReactionBarViewModel,
    modifier: Modifier = Modifier,
) {
    val viewModelState by rememberUpdatedState(viewModel)

    // Image
    FlowRow(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (kind in ReactionKind.entries) {
            ReactionButtonItem(kind, viewModelState) { isProcessing ->
                viewModelState.launchInBackgroundAnimated(isProcessing) {
                    react(kind)
                }
            }
        }
    }
}

@Composable
private fun RowScope.ReactionButtonItem(
    kind: ReactionKind,
    viewModel: ReactionBarViewModel,
    onClick: (isProcessing: MutableState<Boolean>) -> Unit,
) {
    val reaction by remember(viewModel, kind) { viewModel.reaction(kind) }.collectAsState()
    val alwaysOpen by viewModel.reactionListOpen

    val count = reaction.count
    val isSelf = reaction.isSelf
    val onClickUpdated by rememberUpdatedState(onClick)
    AnimatedVisibility(alwaysOpen || count != 0) {
        val isProcessing = remember { mutableStateOf(false) }
        EmojiChip(reaction.kind, count, isSelf, isProcessing.value, onClick = { onClickUpdated(isProcessing) })
    }
}

@Composable
fun ReactionsIconButton(state: ReactionBarViewModel, modifier: Modifier = Modifier) {
    val stateUpdated by rememberUpdatedState(state)
    IconButton(
        onClick = wrapClearFocus { stateUpdated.switchReactionList() },
        modifier = modifier,
//        modifier = Modifier.then(modifier).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
    ) {
        Icon(Icons.Outlined.AddReaction, "Interaction Button") // tint = MaterialTheme.colorScheme.onPrimary
    }
}

@Composable
private fun EmojiChip(
    kind: ReactionKind,
    count: Int,
    isSelf: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    val isLoadingState by rememberUpdatedState(isLoading)
    val onClickState by rememberUpdatedState(onClick)
    ElevatedFilterChip(
        selected = isSelf,
        onClick = wrapClearFocus {
            if (!isLoadingState) onClickState()
        },
        leadingIcon = {
            ReactionPresentation(kind)
        },
        enabled = !isLoading,
        label = {
            if (isLoading) {
                Box(Modifier.size(16.dp)) {
                    CircularProgressIndicator()
                }
            } else {
                Text("$count")
            }
        },
        border = FilterChipDefaults.filterChipBorder(borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    )
}


@Composable
fun ReactionPresentation(reactionKind: ReactionKind) {
    ProvideTextStyle(
        TextStyle(
            fontFamily = EMOJI_FONT,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            background = Color.Transparent,
        )
    ) {
        when (reactionKind) {
            ReactionKind.PLUS_ONE -> Text("\uD83D\uDC4D")
            ReactionKind.MINUS_ONE -> Text("\uD83D\uDC4E")
            ReactionKind.SMILE -> Text("\uD83D\uDE04")
            ReactionKind.CELEBRATION -> Text("\uD83C\uDF89")
            ReactionKind.THINKING -> Text("\uD83E\uDD14")
            ReactionKind.HEART -> Text("❤\uFE0F")
            ReactionKind.ROCKET -> Text("\uD83D\uDE80")
            ReactionKind.EYES -> Text("\uD83D\uDC40")
        }
    }
}
