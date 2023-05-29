package org.solvo.web.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
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
    fontSize: TextUnit = DEFAULT_RICH_EDITOR_FONT_SIZE,
    propagateScrollState: ScrollState? = null,
    scrollOrientation: Orientation = Orientation.Vertical,
    isInDarkTheme: Boolean = rememberUpdatedState(LocalSolvoWindow.current.isInDarkMode()).value,
) {
    val state = rememberRichEditorState()
    RichEditor(
        modifier, state,
        displayMode = RichEditorDisplayMode.PREVIEW_ONLY,
        isToolbarVisible = false,
        propagateScrollState = propagateScrollState,
        scrollOrientation = scrollOrientation,
        isInDarkTheme = isInDarkTheme
    )
    LaunchedEffect(true) {
        hidePreviewCloseButton(state.richEditor)
    }
    val density by rememberUpdatedState(LocalDensity.current)
    LaunchedEffect(fontSize) {
        state.richEditor.setFontSize(fontSize, density)
    }
    LaunchedEffect(text) {
        try {
            state.setContentMarkdown(text)
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
