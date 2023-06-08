package org.solvo.web.comments.commentCard.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.solvo.web.requests.client
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.ICommentDownstream
import org.solvo.model.api.communication.LightCommentDownstream
import org.solvo.model.api.communication.Reaction
import org.solvo.web.utils.DateFormatter
import org.solvo.web.viewModel.AbstractViewModel

sealed class CommentCardViewModel<T : ICommentDownstream>(
    initial: T?
) : AbstractViewModel() {
    val comment: MutableStateFlow<T?> = MutableStateFlow(initial)

    val content = comment.mapNotNull { it?.content }.shareInBackground()
    val author = comment.mapNotNull { it?.author }.shareInBackground()
}


@Composable
fun rememberFullCommentCardViewModel(value: CommentDownstream? = null): FullCommentCardViewModel {
    val model = remember { FullCommentCardViewModel(value) }
    key(value) {
        SideEffect {
            model.comment.value = value
        }
    }
    return model
}

class FullCommentCardViewModel(initial: CommentDownstream? = null) : CommentCardViewModel<CommentDownstream>(initial) {
    val lastCommentTime = comment.mapNotNull { it?.lastCommentTime }
    val postTime = comment.mapNotNull { it?.postTime }.shareInBackground()
    val postTimeFormatted = postTime.mapNotNull { DateFormatter.format(it) }.shareInBackground()

    private val localReactions =
        MutableSharedFlow<List<Reaction>>(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val reactions = merge(
        comment.filterNotNull()
            .mapNotNull { client.comments.getReactions(it.coid) },
        localReactions
    ).shareInBackground()

    fun setReactions(reactions: List<Reaction>) {
        localReactions.tryEmit(reactions)
    }
}

@Composable
fun rememberLightCommentCardViewModel(value: LightCommentDownstream? = null): LightCommentCardViewModel {
    val model = remember { LightCommentCardViewModel(value) }
    key(value) {
        SideEffect {
            model.comment.value = value
        }
    }
    return model
}

class LightCommentCardViewModel(initial: LightCommentDownstream? = null) :
    CommentCardViewModel<LightCommentDownstream>(initial)