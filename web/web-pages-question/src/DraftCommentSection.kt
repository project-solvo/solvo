package org.solvo.web

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.solvo.model.utils.NonBlankString
import org.solvo.web.comments.commentCard.DraftCommentCard
import org.solvo.web.document.History
import org.solvo.web.editor.DEFAULT_RICH_EDITOR_FONT_SIZE
import org.solvo.web.editor.RichEditor
import org.solvo.web.editor.RichEditorDisplayMode
import org.solvo.web.editor.RichEditorState
import org.solvo.web.requests.client
import org.solvo.web.ui.foundation.ifThen
import org.solvo.web.ui.foundation.ifThenElse

@Composable
fun DraftCommentSection(
    isEditorVisible: Boolean,
    showEditor: () -> Unit,
    onSubmitComment: (NonBlankString) -> Unit,
    editorState: RichEditorState,
) {
    val onSubmitCommentUpdated by rememberUpdatedState(onSubmitComment)
    val showEditorUpdated by rememberUpdatedState(showEditor)

    DraftCommentCard(Modifier.padding(bottom = 16.dp)) {
        RichEditor(
            Modifier.fillMaxWidth()
                .ifThenElse(
                    isEditorVisible,
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
                if (isEditorVisible) {
                    if (!client.isLoginIn()) {
                        History.navigate {
                            auth()
                        }
                    } else {
                        if (!contentMarkdown.isNullOrBlank()) {
                            NonBlankString.fromStringOrNull(contentMarkdown)?.let(onSubmitCommentUpdated)
                        }
                    }
                } else {
                    showEditorUpdated()
                }
            },
            Modifier.align(Alignment.End).animateContentSize()
                .ifThen(!isEditorVisible) { fillMaxWidth() }
                .ifThen(isEditorVisible) { padding(top = 12.dp).wrapContentSize() }
        ) {
            Text("Add Comment")
        }
    }
}
