package org.solvo.web.comments.subComments

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.solvo.model.CommentDownstream
import org.solvo.web.comments.commentCard.CommentSummaryCard
import org.solvo.web.comments.commentCard.components.CommentCardContent
import org.solvo.web.comments.commentCard.viewModel.rememberFullCommentCardViewModel
import org.solvo.web.comments.showMore.ShowMoreSwitch
import org.solvo.web.comments.showMore.ShowMoreSwitchState

@Composable
fun CommentColumn(
    items: List<CommentDownstream>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        for (item in items) {
            val state: ShowMoreSwitchState = remember { ShowMoreSwitchState() }

            var hasOverflow by remember { mutableStateOf(false) }

            CommentSummaryCard(
                rememberFullCommentCardViewModel(item),
                state,
                Modifier.wrapContentHeight().fillMaxWidth(),
                showMoreSwitch = if (hasOverflow || state.showingMore.value) {
                    {
                        ShowMoreSwitch(it)
                    }
                } else null
            ) { backgroundColor ->
                CommentCardContent(
                    item,
                    backgroundColor,
                    when {
                        state.showingMore.value -> Modifier.wrapContentHeight()
                        hasOverflow -> Modifier.heightIn(max = 200.dp)
                        else -> Modifier.heightIn(max = 200.dp)
                    },
                    onLayout = { hasOverflow = hasVisualOverflow }
                )
            }
        }
    }
}
