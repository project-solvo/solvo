package org.solvo.web.comments.commentCard.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.solvo.model.CommentDownstream
import org.solvo.web.editor.RichEditorLayoutResult
import org.solvo.web.editor.RichText
import org.solvo.web.editor.rememberRichEditorLoadedState
import org.solvo.web.ui.OverlayLoadableContent

@Composable
fun CommentCardContent(
    item: CommentDownstream,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onLayout: (RichEditorLayoutResult.() -> Unit)? = null,
) {
    key(item.coid) { // redraw editor when item id changed (do not reuse)
        val loadedState = rememberRichEditorLoadedState()
        OverlayLoadableContent(
            !loadedState.isReady,
            loadingContent = { LinearProgressIndicator(Modifier.height(2.dp)) }
        ) {
            RichText(
                item.content,
                modifier = modifier.fillMaxWidth(),
                onEditorLoaded = loadedState.onEditorLoaded,
                onTextUpdated = loadedState.onTextChanged,
                onLayout = onLayout,
                backgroundColor = backgroundColor,
                showScrollbar = true,
            )
        }
    }
}
