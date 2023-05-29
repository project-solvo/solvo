package org.solvo.web.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.browser.document
import kotlinx.coroutines.CancellationException
import org.intellij.lang.annotations.Language
import org.solvo.web.editor.impl.RichEditor
import org.solvo.web.ui.LocalSolvoWindow
import org.solvo.web.ui.isInDarkMode
import org.w3c.dom.asList

@Composable
fun RichText(
    @Language("markdown") text: String,
    modifier: Modifier = Modifier,
    onActualContentSizeChange: (DpSize) -> Unit = {},
    fontSize: TextUnit = DEFAULT_RICH_EDITOR_FONT_SIZE,
    propagateScrollState: ScrollState? = null,
    scrollOrientation: Orientation = Orientation.Vertical,
    isInDarkTheme: Boolean = LocalSolvoWindow.current.isInDarkMode(),
    backgroundColor: Color = Color.Unspecified,
    showScrollbar: Boolean = true,
) {
    val state = rememberRichEditorState(0.dp)
    val density by rememberUpdatedState(LocalDensity.current)

    RichEditor(
        modifier, state,
        displayMode = RichEditorDisplayMode.PREVIEW_ONLY,
        isToolbarVisible = false,
        propagateScrollState = propagateScrollState,
        scrollOrientation = scrollOrientation,
        isInDarkTheme = isInDarkTheme,
        onSizeChanged = {
            state.richEditor.resizeToWrapPreviewContent {
                onActualContentSizeChange(it)
            }
        },
        backgroundColor = backgroundColor
    )
    LaunchedEffect(true) {
        hidePreviewCloseButton(state.richEditor)
    }
    LaunchedEffect(fontSize) {
        state.richEditor.setFontSize(fontSize, density)
    }
    LaunchedEffect(showScrollbar) {
        state.richEditor.setShowScrollBar(showScrollbar)
    }
    LaunchedEffect(text) {
        try {
            state.setPreviewMarkdownAndClip(text) {
                onActualContentSizeChange(it)
            }
//            state.richEditor.resizeToWrapContent(density)
        } catch (e: CancellationException) {
            println("Cancelled: ${state.richEditor.id}")
        }
    }
}

private suspend fun hidePreviewCloseButton(richEditor: RichEditor) {
    richEditor.onEditorLoaded {
        document.getElementById(richEditor.id)
            ?.getElementsByClassName("editormd-preview-close-btn")
            ?.asList()
            ?.forEach {
                it.asDynamic().style.display = "none"
            } ?: return@onEditorLoaded
    }
}
