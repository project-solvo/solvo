package org.solvo.web.comments.reactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.solvo.model.Reaction
import org.solvo.model.ReactionKind
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.theme.EMOJI_FONT

@Composable
fun ReactionBar(
    reactions: List<Reaction>,
    modifier: Modifier = Modifier,
) {
    val state: ReactionBarState = remember { ReactionBarState(reactions) }

    // Image
    Column(modifier) {
        FlowRow(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = wrapClearFocus { state.switchReactionList() },
            ) {
                Icon(Icons.Filled.EmojiEmotions, "Interaction Button")
            }

            for (kind in ReactionKind.entries) {
                val reaction by state.reaction(kind).collectAsState(null)
                val count by remember {
                    derivedStateOf {
                        reaction?.count ?: 0
                    }
                }
                val isSelf by remember { derivedStateOf { reaction?.self ?: false } }
                val reactionListOpen by state.reactionListOpen
                AnimatedVisibility(reactionListOpen || count != 0) {
                    EmojiChip(kind, count, isSelf, onClick = {
                        state.react(kind)
                    })
                }
            }
        }
    }


}

@Composable
private fun EmojiChip(
    kind: ReactionKind,
    count: Int,
    isSelf: Boolean,
    onClick: () -> Unit,
) {
    ElevatedFilterChip(
        selected = isSelf,
        onClick = wrapClearFocus(onClick),
        leadingIcon = { ReactionPresentation(kind) },
        label = { Text("$count") },
        modifier = Modifier.padding(4.dp),
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
