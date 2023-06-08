package org.solvo.web.comments.subComments

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.*
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel

@Stable
class CommentColumnViewModel(
    initialCommentDownstream: CommentDownstream? = null,
) : AbstractViewModel() {
    val commentDownstream: MutableStateFlow<CommentDownstream?> = MutableStateFlow(initialCommentDownstream)


    private val localAllSubComments =
        MutableStateFlow<StateFlow<List<CommentDownstream>>>(MutableStateFlow(emptyList()))
    val remoteAllSubComments: Flow<StateFlow<List<CommentDownstream>>> = commentDownstream.filterNotNull()
        .map { comment ->
            comment.allSubCommentIds.asFlow()
                .filterNot { it.value.isBlank() }
                .mapNotNull {
                    client.comments.getComment(it)
                }.runningList().stateInBackground(initialValue = emptyList())
        }

    fun addLocalSubComment(subComment: CommentDownstream) {
        localAllSubComments.value = MutableStateFlow(listOf(subComment) + allSubComments.value.value)
    }

    fun setLocalSubComments(subComments: List<CommentDownstream>) {
        localAllSubComments.value = MutableStateFlow(subComments)
    }

    val allSubComments: StateFlow<StateFlow<List<CommentDownstream>>> = merge(
        localAllSubComments,
        remoteAllSubComments
    ).stateInBackground(initialValue = MutableStateFlow(emptyList()))
}