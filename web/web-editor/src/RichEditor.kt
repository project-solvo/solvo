package org.solvo.web.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch
import org.solvo.web.ui.LocalSolvoWindow
import org.solvo.web.ui.isInDarkMode


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
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current
    Box(modifier
        .onGloballyPositioned {
            richEditorState.richEditor.setPosition(it.positionInWindow(), density)
            scope.launch {
                richEditorState.richEditor.setEditorBounds(it.boundsInRoot(), density)

                println(
                    "onGloballyPositioned: " +
                            "boundsInParent=${it.boundsInParent()}, " +
                            "boundsInRoot=${it.boundsInRoot()}"
                )
            }
        }
        .onSizeChanged {
            scope.launch {
                richEditorState.richEditor.setEditorSize(it, density)
            }
        }
    )
}

