package org.solvo.web.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import org.solvo.web.document.LocalSolvoWindow
import org.solvo.web.document.isInDarkMode

@Composable
fun RichText(
    text: String,
    modifier: Modifier = Modifier,
    isInDarkTheme: Boolean = rememberUpdatedState(LocalSolvoWindow.current.isInDarkMode()).value,
) {
    val state = rememberRichEditorState()
    LaunchedEffect(text) {
        state.contentMarkdown = text
    }
    RichEditor(
        modifier, state,
        displayMode = RichEditorDisplayMode.PREVIEW_ONLY,
        isToolbarVisible = false,
        isInDarkTheme = isInDarkTheme
    )
}
