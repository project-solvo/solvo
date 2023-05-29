package org.solvo.web.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.solvo.web.ui.LocalSolvoWindow
import org.solvo.web.ui.foundation.get
import org.solvo.web.ui.isInDarkMode


@Composable
fun RichEditor(
    modifier: Modifier = Modifier,
    richEditorState: RichEditorState = rememberRichEditorState(),
    displayMode: RichEditorDisplayMode = RichEditorDisplayMode.EDIT_PREVIEW,
    isToolbarVisible: Boolean = true,
    propagateScrollState: ScrollState? = null,
    scrollOrientation: Orientation = Orientation.Vertical,
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
    val scope = rememberCoroutineScope()
    LaunchedEffect(propagateScrollState) {
        richEditorState.richEditor.onScroll(density) {
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                propagateScrollState?.scrollBy(it[scrollOrientation])
            }
        }
    }

    Box(
        modifier
            .onGloballyPositioned {
                richEditorState.richEditor.setPosition(it.positionInWindow(), density)
                scope.launch {
                    richEditorState.richEditor.setEditorBounds(it.boundsInRoot(), density)

//                println(
//                    "onGloballyPositioned: " +
//                            "boundsInParent=${it.boundsInParent()}, " +
//                            "boundsInRoot=${it.boundsInRoot()}"
//                )
            }
        }
        .onSizeChanged {
            scope.launch {
                richEditorState.richEditor.setEditorSize(it, density)
            }
        }
    )
}

