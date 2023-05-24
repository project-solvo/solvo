package org.solvo.web.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
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
        richEditorState.isInDarkTheme = isInDarkTheme
    }
    LaunchedEffect(displayMode) {
        richEditorState.displayMode = displayMode
    }
    LaunchedEffect(isToolbarVisible) {
        richEditorState.isToolbarVisible = isToolbarVisible
    }

    Text("Sample editor")
    val density = LocalDensity.current
    Box(modifier
        .padding(32.dp)
        .onGloballyPositioned {
            richEditorState.richEditor.setPosition(it.positionInWindow(), density)
            richEditorState.richEditor.setSize(it.size, density)
        }
    ) {
        Spacer(Modifier.fillMaxSize())
    }
}

