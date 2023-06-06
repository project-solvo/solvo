package org.solvo.web.comments

import kotlinx.coroutines.flow.*
import org.solvo.model.CommentDownstream
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel

class CommentColumnViewModel(
    initialCommentDownstream: CommentDownstream? = null,
) : AbstractViewModel() {
    val commentDownstream: MutableStateFlow<CommentDownstream?> = MutableStateFlow(initialCommentDownstream)

    val allSubComments: SharedFlow<Flow<List<CommentDownstream>>> = commentDownstream.filterNotNull()
        .map { comment ->
            comment.allSubCommentIds.asFlow().mapNotNull {
                client.comments.getComment(it)
            }.runningList()
        }
        .shareInBackground()
}