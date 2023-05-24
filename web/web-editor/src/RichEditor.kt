package org.solvo.web.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import org.solvo.web.document.LocalSolvoWindow
import org.solvo.web.document.isInDarkMode


@Composable
fun RichEditor(
    modifier: Modifier = Modifier,
    richEditorState: RichEditorState = rememberRichEditorState(),
    displayMode: RichEditorDisplayMode = RichEditorDisplayMode.EDIT_PREVIEW,
    isToolbarVisible: Boolean = true,
    isInDarkTheme: Boolean = rememberUpdatedState(LocalSolvoWindow.current.isInDarkMode()).value,
) {
    LaunchedEffect(isInDarkTheme) {
        richEditorState.richEditor.setInDarkTheme(isInDarkTheme)
    }
    LaunchedEffect(displayMode) {
        richEditorState.richEditor.setDisplayMode(displayMode)
    }
    LaunchedEffect(isToolbarVisible) {
        richEditorState.richEditor.setToolbarVisible(isToolbarVisible)
    }

    val density = LocalDensity.current
    Box(modifier
        .onGloballyPositioned {
            richEditorState.richEditor.setPosition(it.positionInWindow(), density)
            richEditorState.richEditor.setSize(it.size, density)
        }
    )
}

