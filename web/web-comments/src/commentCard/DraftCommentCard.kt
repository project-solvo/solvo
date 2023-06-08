package org.solvo.web.comments.commentCard

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.solvo.web.comments.commentCard.impl.CommentCard

/**
 * "Add Comment" Card
 */
@Composable
fun DraftCommentCard(
    modifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
    showMoreSwitch: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    CommentCard(
        paddings = CommentCardPaddings.Small,
        modifier = modifier,
        authorLine = null,
        onClickCard = onClickCard,
        showMoreSwitch = showMoreSwitch,
        subComments = null,
        content = content,
    )
}
