package org.solvo.web

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.CommentUpstream
import org.solvo.model.utils.NonBlankString
import org.solvo.web.comments.commentCard.DraftCommentCard
import org.solvo.web.editor.RichEditor
import org.solvo.web.editor.RichEditorDisplayMode
import org.solvo.web.editor.rememberRichEditorState
import org.solvo.web.requests.client
import org.solvo.web.ui.foundation.ifThen
import org.solvo.web.viewModel.LoadingUuidItem

@Composable
fun DraftCommentSection(
    showEditor: Boolean,
    onShowEditorChange: (Boolean) -> Unit,
    backgroundScope: CoroutineScope,
    pagingState: ExpandablePagingState<LoadingUuidItem<CommentDownstream>>,
) {
    DraftCommentCard(Modifier.padding(bottom = 16.dp)) {
        val editorHeight by animateDpAsState(if (showEditor) 200.dp else 0.dp)

        val editorState = rememberRichEditorState(isEditable = true)
        RichEditor(
            Modifier.fillMaxWidth().height(editorHeight),
            displayMode = RichEditorDisplayMode.EDIT_ONLY,
            isToolbarVisible = false,
            state = editorState,
            fontSize = 18.sp,
        )

        Button(
            {
                if (showEditor) {
                    if (!client.isLoginIn()) {
                        client.jumpToLoginPage()
                    } else {
                        if (editorState.contentMarkdown.isNotBlank()) {
                            pagingState.currentContent.value.firstOrNull()?.let { comment ->
                                val upstream = CommentUpstream(
                                    content = NonBlankString.fromStringOrNull(editorState.contentMarkdown)
                                        ?: return@let,
                                )
                                backgroundScope.launch {
                                    client.comments.postComment(comment.coid, upstream)
                                }
                            }
                        }
                    }
                }
                if (editorState.contentMarkdown.isNotBlank()) {
                    onShowEditorChange(!showEditor)
                }
            }, Modifier.align(Alignment.End).animateContentSize()
                .ifThen(!showEditor) { fillMaxWidth() }
                .ifThen(showEditor) { padding(top = 12.dp).wrapContentSize() }
        ) {
            Text("Add Comment")
        }
    }
}
