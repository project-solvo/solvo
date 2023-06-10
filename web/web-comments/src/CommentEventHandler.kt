package org.solvo.web.comments

import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.events.CommentEvent
import org.solvo.model.api.events.RemoveCommentEvent
import org.solvo.model.api.events.UpdateCommentEvent
import org.solvo.web.utils.replacedOrPrepend
import org.solvo.web.viewModel.LoadingUuidItem

class CommentEventHandler(
    private val getCurrentAllComments: () -> List<LoadingUuidItem<CommentDownstream>>,
) {
    fun handleEvent(event: CommentEvent): List<LoadingUuidItem<CommentDownstream>> {
        return when (event) {
            is UpdateCommentEvent -> {
                getCurrentAllComments().replacedOrPrepend(
                    LoadingUuidItem(
                        event.commentCoid,
                        event.commentDownstream
                    )
                )
            }

            is RemoveCommentEvent -> getCurrentAllComments()
                .filterNot { it.coid == event.commentCoid }
        }
    }
}