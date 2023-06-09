package org.solvo.web.comments.subComments

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.*
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.events.CommentEvent
import org.solvo.model.api.events.UpdateCommentEvent
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.LoadingUuidItem

@Stable
class CommentColumnViewModel(
    initialCommentDownstream: Flow<CommentDownstream?>,
    events: Flow<CommentEvent>,
) : AbstractViewModel() {
    private val commentDownstream: StateFlow<CommentDownstream?> = initialCommentDownstream.stateInBackground()

    private val changes = events
        .filter { it.parentCoid == commentDownstream.value?.parent }
        .map {
            handleEvent(it)
        }

    private fun handleEvent(event: CommentEvent): List<LoadingUuidItem<CommentDownstream>> {
        return when (event) {
            is UpdateCommentEvent -> {
                listOf(
                    LoadingUuidItem(
                        event.commentCoid,
                        event.commentDownstream
                    )
                ) + allSubComments.value
            }
        }
    }

    private val remoteAllSubComments: Flow<List<LoadingUuidItem<CommentDownstream>>> = commentDownstream.filterNotNull()
        .mapLatestSupervised { comment ->
            comment.allSubCommentIds
                .filterNot { it.value.isBlank() }
                .mapLoadIn(this) {
                    client.comments.getComment(it)
                }
        }

    val allSubComments: StateFlow<List<LoadingUuidItem<CommentDownstream>>> =
        merge(changes, remoteAllSubComments).stateInBackground(initialValue = emptyList())
}
