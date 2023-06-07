package org.solvo.web.comments.commentCard.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.solvo.web.comments.commentCard.CommentCardPaddings
import org.solvo.web.comments.showMore.ShowMoreSwitchState
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon


@Stable
internal val CommentCardShape = RoundedCornerShape(16.dp)

@Suppress("NAME_SHADOWING")
@Composable
internal fun CommentCard(
    paddings: CommentCardPaddings,
    state: ShowMoreSwitchState,
    modifier: Modifier = Modifier,
    authorLine: (@Composable () -> Unit)? = null,
    onClickCard: () -> Unit = {},
    showMoreSwitch: (@Composable (state: ShowMoreSwitchState) -> Unit)? = null,
    subComments: (@Composable () -> Unit)? = null,
    reactions: @Composable (() -> Unit)? = null,
    contentModifier: Modifier = Modifier,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    val backgroundColor = commentCardBackgroundColor()
    val onClickCard by rememberUpdatedState(onClickCard)

    Card(
        modifier.clickable(indication = null, onClick = onClickCard),
        shape = CommentCardShape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(Modifier.padding(top = paddings.top).padding(paddings.headLinePadding)) {
            authorLine?.invoke()
        }

        // content
        Column(contentModifier.padding(paddings.contentPadding)) {
            content(backgroundColor)
        }

        if (showMoreSwitch != null) {
            Column(
                Modifier.padding(horizontal = 16.dp).padding(top = 6.dp).padding(bottom = 6.dp)
                    .cursorHoverIcon(CursorIcon.POINTER)
            ) {
                showMoreSwitch.invoke(state)
            }
        }

        if (subComments != null) {
            Column(
                Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    .background(Color(0x212121), shape = CommentCardShape)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                subComments.invoke()
            }
        }

        if (subComments == null) {
            Spacer(Modifier.height(paddings.bottom).fillMaxWidth())
        }

        reactions?.invoke()
    }
}


@Composable
private fun commentCardBackgroundColor() = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
