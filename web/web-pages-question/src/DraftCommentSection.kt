package org.solvo.web

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.CommentKind
import org.solvo.model.api.communication.CommentUpstream
import org.solvo.model.utils.NonBlankString
import org.solvo.web.comments.commentCard.DraftCommentCard
import org.solvo.web.document.History
import org.solvo.web.editor.DEFAULT_RICH_EDITOR_FONT_SIZE
import org.solvo.web.editor.RichEditor
import org.solvo.web.editor.RichEditorDisplayMode
import org.solvo.web.editor.rememberRichEditorState
import org.solvo.web.requests.client
import org.solvo.web.ui.foundation.ifThen
import org.solvo.web.ui.foundation.ifThenElse
import org.solvo.web.viewModel.LoadingUuidItem

@Composable
fun DraftCommentSection(
    showEditor: Boolean,
    onShowEditorChange: (Boolean) -> Unit,
    backgroundScope: CoroutineScope,
    pagingState: ExpandablePagingState<LoadingUuidItem<CommentDownstream>>,
) {
    DraftCommentCard(Modifier.padding(bottom = 16.dp)) {
        val editorState =
            rememberRichEditorState(isEditable = true, showToolbar = true, fontSize = DEFAULT_RICH_EDITOR_FONT_SIZE)
        RichEditor(
            Modifier.fillMaxWidth()
                .ifThenElse(
                    showEditor,
                    then = { wrapContentHeight().heightIn(min = 300.dp) },
                    `else` = { height(0.dp) }),
            displayMode = RichEditorDisplayMode.EDIT_ONLY,
            fontSize = DEFAULT_RICH_EDITOR_FONT_SIZE,
            state = editorState,
            isToolbarVisible = true,
            showScrollbar = false,
        )

        Button(
            {
                val contentMarkdown = editorState.contentMarkdown
                if (showEditor) {
                    if (!client.isLoginIn()) {
                        History.navigate {
                            auth()
                        }
                    } else {
                        if (!contentMarkdown.isNullOrBlank()) {
                            pagingState.currentContent.value.firstOrNull()?.let { comment ->
                                val upstream = CommentUpstream(
                                    content = NonBlankString.fromStringOrNull(contentMarkdown)
                                        ?: return@let,
                                )
                                backgroundScope.launch {
                                    client.comments.post(comment.coid, upstream, CommentKind.COMMENT)
                                }
                            }
                        }
                    }
                }
                if (!contentMarkdown.isNullOrBlank()) {
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
