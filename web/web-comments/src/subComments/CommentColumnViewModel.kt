package org.solvo.web.comments.subComments

import kotlinx.coroutines.flow.*
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel

class CommentColumnViewModel(
    initialCommentDownstream: CommentDownstream? = null,
) : AbstractViewModel() {
    val commentDownstream: MutableStateFlow<CommentDownstream?> = MutableStateFlow(initialCommentDownstream)

    val allSubComments: SharedFlow<Flow<List<CommentDownstream>>> = commentDownstream.filterNotNull()
        .map { comment ->
            comment.allSubCommentIds.asFlow()
                .filterNot { it.value.isBlank() }
                .mapNotNull {
                    client.comments.getComment(it)
                }.runningList()
        }
        .shareInBackground()
}