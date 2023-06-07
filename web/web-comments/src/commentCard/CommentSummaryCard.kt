package org.solvo.web.comments.commentCard

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.solvo.web.comments.commentCard.components.AuthorLineThin
import org.solvo.web.comments.commentCard.impl.CommentCard
import org.solvo.web.comments.commentCard.viewModel.FullCommentCardViewModel
import org.solvo.web.comments.showMore.ShowMoreSwitchState
import org.solvo.web.ui.image.RoundedUserAvatar
import org.solvo.web.utils.DateFormatter


/**
 * In comment column (sidebar)
 */
@Composable
fun CommentSummaryCard(
    viewModel: FullCommentCardViewModel,
    state: ShowMoreSwitchState = remember { ShowMoreSwitchState() },
    modifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
    showMoreSwitch: (@Composable (state: ShowMoreSwitchState) -> Unit)? = null,
    reactions: (@Composable () -> Unit)? = null,
    content: @Composable (ColumnScope.(backgroundColor: Color) -> Unit),
) {
    CommentCard(
        paddings = CommentCardPaddings.Small,
        state = state,
        modifier = modifier,
        authorLine = {
            val postTime by viewModel.postTime.collectAsState(null)
            val author by viewModel.author.collectAsState(null)
            AuthorLineThin(
                icon = {
                    RoundedUserAvatar(author?.avatarUrl, 20.dp)
                },
                authorName = author?.username ?: "",
                date = postTime?.let { DateFormatter.format(it) } ?: "",
            )
        },
        onClickCard = onClickCard,
        showMoreSwitch = showMoreSwitch,
        reactions = reactions,
        content = content,
    )
}