@file:Suppress("MemberVisibilityCanBePrivate")

package org.solvo.web.editor

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.solvo.web.ui.LocalSolvoWindow
import org.solvo.web.ui.foundation.asIntSize
import org.solvo.web.ui.isInDarkMode

internal const val RichEditorLayoutDebug = false // inline

/**
 * @param onEditorLoaded called when editor is loaded, and actual editor size is known.
 * @param onIntrinsicSizeChanged called when intrinsic size (the size required to paint the full text) is changed.
 * @param onLayout called when the layout is measured.
 * @param propagateScrollState Propagate scroll events to scroll state, to support scrolling.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RichEditor(
    modifier: Modifier = Modifier,
    onEditorLoaded: (() -> Unit)? = null,
    onIntrinsicSizeChanged: ((intrinsicSize: IntSize?) -> Unit)? = null,
    onLayout: (RichEditorLayoutResult.() -> Unit)? = null,
    state: RichEditorState = rememberRichEditorState(isEditable = true),
    displayMode: RichEditorDisplayMode = RichEditorDisplayMode.EDIT_PREVIEW,
    fontSize: TextUnit = DEFAULT_RICH_EDITOR_FONT_SIZE,
    isToolbarVisible: Boolean = true,
    isInDarkTheme: Boolean = LocalSolvoWindow.current.isInDarkMode(),
    backgroundColor: Color = Color.Unspecified,
    showScrollbar: Boolean = true,
    contentColor: Color = LocalContentColor.current,
) {
    val density = LocalDensity.current

    LaunchedEffect(isInDarkTheme, state) {
        state.richEditor.setInDarkTheme(isInDarkTheme)
    }
    LaunchedEffect(displayMode, state, density) {
        state.richEditor.setDisplayMode(displayMode, density)
    }
    LaunchedEffect(isToolbarVisible, state) {
        state.richEditor.setToolbarVisible(isToolbarVisible)
    }
    LaunchedEffect(fontSize, state, density) {
        state.richEditor.setFontSize(fontSize, density)
    }
    LaunchedEffect(contentColor, state) {
        state.richEditor.setContentColor(contentColor)
    }
    LaunchedEffect(showScrollbar, state) {
        state.richEditor.setShowScrollBar(showScrollbar)
    }

    val scope = rememberCoroutineScope()
    LaunchedEffect(state) {
        state.richEditor.bindEvents(density)
    }
    LaunchedEffect(backgroundColor, state) {
        if (backgroundColor.isSpecified) {
            state.richEditor.setBackgroundColor(backgroundColor)
        }
    }
    LaunchedEffect(true, onEditorLoaded, state) {
        if (onEditorLoaded != null) {
            state.richEditor.onEditorLoaded {
                onEditorLoaded.invoke()
            }
        }
    }
    var actualSize: IntSize? by remember { mutableStateOf(null) }

    LaunchedEffect(state, density) {
        state.richEditor.onActualAreaChanged.collect {
            if (RichEditorLayoutDebug) {
                println("listen actual area change: $it ")
            }
            actualSize = (it * density.density).asIntSize()
        }
    }


    Layout(modifier) { _, constraints ->
        val width = (actualSize?.width ?: 100.dp.roundToPx()).coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = (actualSize?.height ?: 100.dp.roundToPx()).coerceIn(constraints.minHeight, constraints.maxHeight)
        if (RichEditorLayoutDebug) {
            println(constraints)
            println("Layout: width=$width, height=$height")
        }
        layout(
            width,
            height,
        ) {
            if (RichEditorLayoutDebug) {
                println("Place: width=$width, height=$height")
            }
            coordinates?.let { coordinates ->
                state.richEditor.setPosition(coordinates.positionInRoot(), density)
                state.richEditor.setEditorBounds(coordinates.boundsInRoot(), density)
            }
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                state.richEditor.setEditorSize(IntSize(width, height), density)
                state.richEditor.setEditorSize(IntSize(width, height), density)

                actualSize = (state.richEditor.awaitActualSize() * density.density).asIntSize()
                onIntrinsicSizeChanged?.invoke(actualSize)
                if (RichEditorLayoutDebug) {
                    println("actualSize changed: $actualSize")
                }
//                    onSizeChanged(coordinates.size)
            }

            coordinates?.let { coordinates ->
                actualSize?.let { fullSize ->
                    onLayout?.invoke(
                        RichEditorLayoutResult(
                            coordinates, constraints, fullSize, IntSize(width, height)
                        )
                    )
                }
            }
        }
    }
}
