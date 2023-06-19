package org.solvo.web.comment

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.events.Event
import org.solvo.web.answer.AnswerCardReactions
import org.solvo.web.comments.commentCard.CommentSummaryCard
import org.solvo.web.comments.commentCard.components.CommentCardContent
import org.solvo.web.comments.commentCard.viewModel.rememberFullCommentCardViewModel
import org.solvo.web.comments.showMore.ShowMoreSwitch
import org.solvo.web.comments.showMore.ShowMoreSwitchState
import org.solvo.web.viewModel.LoadingUuidItem

@Composable
fun CommentColumn(
    items: List<LoadingUuidItem<CommentDownstream>>,
    events: SharedFlow<Event>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        for (item in items) {
            CommentColumnItem(item, events)
        }
    }
}

@Composable
private fun CommentColumnItem(
    item: LoadingUuidItem<CommentDownstream>,
    events: SharedFlow<Event>,
) {
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
        } else null,
        subComments = {
            commentDownstream?.let {
                AnswerCardReactions(it, events)
            }
        }
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
