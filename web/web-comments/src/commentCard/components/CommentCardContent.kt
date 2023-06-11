package org.solvo.web.comments.commentCard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.web.editor.RichEditorLayoutResult
import org.solvo.web.editor.RichText
import org.solvo.web.editor.rememberRichEditorLoadedState
import org.solvo.web.ui.OverlayLoadableContent
import org.solvo.web.ui.foundation.rememberMutableDebouncedState
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CommentCardContent(
    item: CommentDownstream,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    showScrollbar: Boolean = true,
    showFullAnswer: @Composable (() -> Unit)? = null,
    onLayout: (RichEditorLayoutResult.() -> Unit)? = null,
) {
    @Suppress("NAME_SHADOWING")
    val onLayout by rememberUpdatedState(onLayout)

    var richTextHasVisualOverflow by rememberMutableDebouncedState(false, 200.milliseconds)

//    key(item.coid) { // redraw editor when item id changed (do not reuse)
    val loadedState = rememberRichEditorLoadedState()
    OverlayLoadableContent(
        !loadedState.isReady,
        loadingContent = { LinearProgressIndicator(Modifier.height(2.dp)) }
    ) {
        Column {
            RichText(
                item.content,
                modifier = modifier.fillMaxWidth(),
                onEditorLoaded = loadedState.onEditorLoaded,
                onTextUpdated = loadedState.onTextChanged,
                onLayout = {
                    richTextHasVisualOverflow = hasVisualOverflow
                    onLayout?.invoke(this)
                },
                backgroundColor = backgroundColor,
                showScrollbar = showScrollbar,
                fontSize = AuthorNameTextStyle.fontSize,
            )

            if (showFullAnswer != null && richTextHasVisualOverflow) {
//                    Surface(
//                        Modifier.fillMaxWidth().wrapContentHeight(),
//                        color = MaterialTheme.colorScheme.surface.copy(0.7f),
//                    ) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    showFullAnswer()
                }
//                    }
            }
        }
    }
//    }
}
