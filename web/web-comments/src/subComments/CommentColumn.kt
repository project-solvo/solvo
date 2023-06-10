package org.solvo.web.comments.subComments

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.web.comments.commentCard.CommentSummaryCard
import org.solvo.web.comments.commentCard.components.CommentCardContent
import org.solvo.web.comments.commentCard.viewModel.rememberFullCommentCardViewModel
import org.solvo.web.comments.showMore.ShowMoreSwitch
import org.solvo.web.comments.showMore.ShowMoreSwitchState
import org.solvo.web.viewModel.LoadingUuidItem

@Composable
fun CommentColumn(
    items: List<LoadingUuidItem<CommentDownstream>>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        println("CommentColumn recompose: ${items.joinToString { it.ready?.content.toString() }}")
        for (item in items) {
            CommentColumnItem(item)
        }
    }
}

@Composable
private fun CommentColumnItem(item: LoadingUuidItem<CommentDownstream>) {
    val state: ShowMoreSwitchState = remember { ShowMoreSwitchState() }

    var hasOverflow by remember { mutableStateOf(false) }

    val commentDownstream by item.asFlow().collectAsState(null)
    CommentSummaryCard(
        rememberFullCommentCardViewModel(commentDownstream),
        state,
        Modifier.wrapContentHeight().fillMaxWidth(),
        showMoreSwitch = if (hasOverflow || state.showingMore.value) {
            {
                ShowMoreSwitch(it)
            }
        } else null
    ) { backgroundColor ->
        CommentCardContent(
            commentDownstream ?: return@CommentSummaryCard,
            backgroundColor,
            when {
                state.showingMore.value -> Modifier.wrapContentHeight()
                hasOverflow -> Modifier.heightIn(max = 200.dp)
                else -> Modifier.heightIn(max = 200.dp)
            }
        ) { hasOverflow = hasVisualOverflow }
    }
}
