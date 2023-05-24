package org.solvo.web.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import kotlinx.browser.document
import org.solvo.web.document.LocalSolvoWindow
import org.solvo.web.document.isInDarkMode
import org.solvo.web.editor.impl.RichEditor
import org.w3c.dom.asList

@Composable
fun RichText(
    text: String,
    modifier: Modifier = Modifier,
    isInDarkTheme: Boolean = rememberUpdatedState(LocalSolvoWindow.current.isInDarkMode()).value,
) {
    val state = rememberRichEditorState()
    LaunchedEffect(text) {
        state.setContentMarkdown(text)
    }
    RichEditor(
        modifier, state,
        displayMode = RichEditorDisplayMode.PREVIEW_ONLY,
        isToolbarVisible = false,
        isInDarkTheme = isInDarkTheme
    )
    LaunchedEffect(true) {
        hidePreviewCloseButton(state.richEditor)
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
