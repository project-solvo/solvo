package org.solvo.web.editor

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.Language
import org.solvo.web.ui.LocalSolvoWindow
import org.solvo.web.ui.isInDarkMode

/**
 * @param onEditorLoaded called when editor is loaded, and actual editor size is known.
 * @param onIntrinsicSizeChanged called when intrinsic size (the size required to paint the full text) is changed.
 * @param onLayout called when the layout is measured.
 * @param propagateScrollState Propagate scroll events to scroll state, to support scrolling.
 */
@Composable
@Suppress("NAME_SHADOWING")
fun RichText(
    @Language("markdown") text: String,
    modifier: Modifier = Modifier,
    onEditorLoaded: (() -> Unit)? = null,
    onTextUpdated: (() -> Unit)? = null,
    onLayout: (RichEditorLayoutResult.() -> Unit)? = null,
    fontSize: TextUnit = LocalTextStyle.current.fontSize,
    isInDarkTheme: Boolean = LocalSolvoWindow.current.isInDarkMode(),
    backgroundColor: Color = Color.Unspecified,
    showScrollbar: Boolean = true,
    contentColor: Color = LocalContentColor.current,
) {
    val state = rememberRichEditorState(false, 0.dp, fontSize = fontSize)
    val onTextUpdated by rememberUpdatedState(onTextUpdated)

    RichEditor(
        modifier,
        onEditorLoaded = onEditorLoaded,
        onLayout = onLayout,
        displayMode = RichEditorDisplayMode.PREVIEW_ONLY,
        fontSize = fontSize,
        state = state,
        isToolbarVisible = false,
        isInDarkTheme = isInDarkTheme,
        backgroundColor = backgroundColor,
        showScrollbar = showScrollbar,
        contentColor = contentColor,
    )
    LaunchedEffect(state) {
        state.richEditor.hidePreviewCloseButton()
    }
    LaunchedEffect(text, onTextUpdated, state) {
        state.setContentMarkdown(text)
        onTextUpdated?.invoke()
    }
}
