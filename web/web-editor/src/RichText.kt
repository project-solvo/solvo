package org.solvo.web.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material3.LocalContentColor
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
    fontSize: TextUnit = DEFAULT_RICH_EDITOR_FONT_SIZE,
    propagateScrollState: ScrollState? = null,
    scrollOrientation: Orientation = Orientation.Vertical,
    isInDarkTheme: Boolean = LocalSolvoWindow.current.isInDarkMode(),
    backgroundColor: Color = Color.Unspecified,
    showScrollbar: Boolean = true,
    contentColor: Color = LocalContentColor.current,
) {
    val state = rememberRichEditorState(0.dp)
    val onTextUpdated by rememberUpdatedState(onTextUpdated)

    RichEditor(
        modifier,
        onEditorLoaded = onEditorLoaded,
        state = state,
        displayMode = RichEditorDisplayMode.PREVIEW_ONLY,
        isToolbarVisible = false,
        propagateScrollState = propagateScrollState,
        scrollOrientation = scrollOrientation,
        isInDarkTheme = isInDarkTheme,
        backgroundColor = backgroundColor,
        fontSize = fontSize,
        showScrollbar = showScrollbar,
        contentColor = contentColor,
        onLayout = onLayout,
    )
    LaunchedEffect(true) {
        state.richEditor.hidePreviewCloseButton()
    }
    LaunchedEffect(text) {
        state.setContentMarkdown(text)
        onTextUpdated?.invoke()
    }
}
