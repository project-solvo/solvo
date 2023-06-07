package org.solvo.web.comments.commentCard

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.solvo.web.comments.commentCard.impl.CommentCard
import org.solvo.web.comments.showMore.ShowMoreSwitchState

/**
 * "Add Comment" Card
 */
@Composable
fun DraftCommentCard(
    modifier: Modifier = Modifier,
    state: ShowMoreSwitchState = remember { ShowMoreSwitchState() },
    onClickCard: () -> Unit = {},
    showMoreSwitch: (@Composable (state: ShowMoreSwitchState) -> Unit)? = null,
    reactions: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    CommentCard(
        paddings = CommentCardPaddings.Small,
        state = state,
        modifier = modifier,
        authorLine = null,
        onClickCard = onClickCard,
        showMoreSwitch = showMoreSwitch,
        subComments = null,
        reactions = reactions,
        content = content,
    )
}
