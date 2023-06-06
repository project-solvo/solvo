package org.solvo.web.comments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import org.solvo.model.CommentDownstream
import org.solvo.model.ICommentDownstream
import org.solvo.model.LightCommentDownstream
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