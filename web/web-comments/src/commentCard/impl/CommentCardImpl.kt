package org.solvo.web.comments.commentCard.impl

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.solvo.web.comments.commentCard.CommentCardPaddings
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon


@Stable
internal val CommentCardShape = RoundedCornerShape(16.dp)

@Suppress("NAME_SHADOWING")
@Composable
internal fun CommentCard(
    paddings: CommentCardPaddings,
    modifier: Modifier = Modifier,
    backgroundColor: Color = commentCardBackgroundColor(),
    shape: Shape = CommentCardShape,
    authorLine: (@Composable () -> Unit)? = null,
    onClickCard: () -> Unit = {},
    showMoreSwitch: (@Composable () -> Unit)? = null,
    subComments: (@Composable ColumnScope.() -> Unit)? = null,
    contentModifier: Modifier = Modifier,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    val onClickCard by rememberUpdatedState(onClickCard)

    Card(
        modifier.clickable(indication = null, onClick = onClickCard),
        shape = shape,
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
                Modifier.padding(horizontal = 16.dp).padding(vertical = 6.dp)
                    .cursorHoverIcon(CursorIcon.POINTER)
            ) {
                showMoreSwitch.invoke()
            }
        }

        if (subComments != null) {
            Divider(
                Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(0.3f)
            )
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                subComments.invoke(this)
            }
        }

        if (subComments == null) {
            Spacer(Modifier.height(paddings.bottom).fillMaxWidth())
        }
    }
}


@Composable
internal fun commentCardBackgroundColor() = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
