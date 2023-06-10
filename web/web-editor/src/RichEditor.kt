@file:Suppress("MemberVisibilityCanBePrivate")

package org.solvo.web.editor

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import org.solvo.web.editor.impl.ifEditorLoaded
import org.solvo.web.editor.impl.onEditorLoaded
import org.solvo.web.ui.LocalSolvoWindow
import org.solvo.web.ui.foundation.asIntSize
import org.solvo.web.ui.isInDarkMode

internal const val RichEditorLayoutDebug = false // inline

/**
 * @param onEditorLoaded called when editor is loaded, and actual editor size is known.
 * @param onLayout called when the layout is measured.
 */
@Composable
fun RichEditor(
    modifier: Modifier = Modifier,
    onEditorLoaded: (() -> Unit)? = null,
    onLayout: (RichEditorLayoutResult.() -> Unit)? = null,
    state: RichEditorState = rememberRichEditorState(isEditable = true),
    displayMode: RichEditorDisplayMode = RichEditorDisplayMode.EDIT_PREVIEW,
    fontSize: TextUnit = LocalTextStyle.current.fontSize,
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

    LaunchedEffect(state, density) {
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

    val onLayoutState = rememberUpdatedState(onLayout)
    val measurePolicy = remember(state) { RichEditorMeasurePolicy(state, onLayoutState) }

    LaunchedEffect(state, density) {
        state.richEditor.actualSizeFlow.collect {
            if (RichEditorLayoutDebug) {
                println("listen actual area change: $it ")
            }
            measurePolicy.setActualSize((it * density.density).asIntSize())
        }
    }

    Layout(
        modifier
            .onPlaced {
                state.updateEditorBounds(it, density)
            }, measurePolicy
    )


    LaunchedEffect(true) {
        // Editor may not be ready on layout, hence its size may not update. 
        // So we ensure here editor size is updated.
        with(state.richEditor) {
            onEditorLoaded {
                setEditorSize(measurePolicy.previousLayoutResult.filterNotNull().first().intrinsicSize, density)
            }
        }
    }
}

private fun RichEditorState.updateEditorBounds(
    it: LayoutCoordinates,
    density: Density
) {
    richEditor.setPosition(it.positionInWindow(), density)
    val parentBounds = it.parentLayoutCoordinates?.boundsInWindow() ?: Rect.Zero

    richEditor.setEditorBounds(it.boundsInParent().run {
        copy(
            left = left + parentBounds.left,
            top = top + parentBounds.top,
            right = right + parentBounds.right,
            bottom = bottom + parentBounds.bottom,
        )
    }, density)
}

internal class RichEditorMeasurePolicy(
    private val state: RichEditorState,
    private val onLayoutState: State<(RichEditorLayoutResult.() -> Unit)?>,
) : MeasurePolicy {
    private val _actualSize: MutableState<IntSize?> = mutableStateOf(null)
    val actualSize: IntSize? get() = _actualSize.value

    val previousLayoutResult = MutableStateFlow<RichEditorLayoutResult?>(null)
    fun setActualSize(size: IntSize?) {
        if (RichEditorLayoutDebug) {
            println("actualSize changed: $actualSize")
        }

        _actualSize.value = size
    }

    private fun notifyLayoutChange(
        intrinsicSize: IntSize, layoutSize: IntSize,
    ) {
        // notify onLayoutState
        if (previousLayoutResult.value?.canReuse(intrinsicSize, layoutSize) == true) {
            // layout did not change
            if (RichEditorLayoutDebug) println("layout did not change")
        } else {
            // new layout
            if (RichEditorLayoutDebug) println("new layout: intrinsicSize=$intrinsicSize, layoutSize=$layoutSize")

            val new = RichEditorLayoutResult(intrinsicSize, layoutSize)
            previousLayoutResult.value = new
            onLayoutState.value?.invoke(new)
        }
    }


    @OptIn(ExperimentalComposeUiApi::class)
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val actualSize = actualSize
        val width = (actualSize?.width ?: 100.dp.roundToPx()).coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = (actualSize?.height ?: 100.dp.roundToPx()).coerceIn(constraints.minHeight, constraints.maxHeight)
        if (RichEditorLayoutDebug) {
            println(constraints)
            println("Layout: width=$width, height=$height")
        }
        return layout(
            width,
            height,
        ) {
            val density = this@measure
            if (RichEditorLayoutDebug) {
                println("Place: width=$width, height=$height")
            }

            coordinates?.let { state.updateEditorBounds(it, density) }

            val layoutSize = IntSize(width, height)

            if (actualSize == null) {
                notifyLayoutChange(layoutSize, layoutSize)
            } else {
                notifyLayoutChange(actualSize, layoutSize)
            }

            with(state.richEditor) {
                ifEditorLoaded {
                    setEditorSize(layoutSize, density)
                }
            }
        }
    }
}
