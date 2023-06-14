package org.solvo.web.ui.foundation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.cursorHoverIcon


@Composable
fun IconOnDivider(
    width: Dp,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
    divider: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.width(width), contentAlignment = Alignment.Center) {
        divider()
        Box(propagateMinConstraints = false) {
            Image(
                imageVector, "Icon",
                contentScale = ContentScale.Crop,
                colorFilter = colorFilter
            )
        }
    }
}

@Composable
fun VerticalDraggableDivider(
    onDrag: (Dp) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 8.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(width),
    onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit = {},
) {
    val density = LocalDensity.current
    val dragState = rememberDraggableState { onDrag(with(density) { it.toDp() }) }

    IconOnDivider(
        width, Icons.Default.MoreVert,
        colorFilter = ColorFilter.tint(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                .compositeOver(backgroundColor)
        ),
        modifier = modifier,
    ) {
        Divider(
            Modifier
                .fillMaxHeight()
                .width(width)
                .draggable(dragState, Orientation.Horizontal, onDragStopped = onDragStopped)
                .cursorHoverIcon(CursorIcon.COL_RESIZE),
            color = backgroundColor
        )
    }
}

@Composable
fun HorizontallyDivided(
    left: @Composable ColumnScope.() -> Unit,
    right: @Composable ColumnScope.() -> Unit,
    isLeftVisible: Boolean = true,
    isRightVisible: Boolean = true,
    initialLeftWeight: Float = 1.0f - 0.618f,
    leftWidthRange: @Composable BoxWithConstraintsScope.() -> ClosedRange<Dp> = { 0.dp..Dp.Infinity },
    modifier: Modifier = Modifier,
    dividerModifier: Modifier = Modifier,
) {
    BoxWithConstraints {
        val constrains = this
        var leftWidth by remember(initialLeftWeight) { mutableStateOf(maxWidth * initialLeftWeight) }
        Row(modifier) {
            val range by rememberUpdatedState(leftWidthRange(constrains))
            AnimatedVisibility(
                isLeftVisible,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it },
            ) {
                Column(if (isRightVisible) Modifier.width(leftWidth.coerceIn(range)) else Modifier.fillMaxWidth()) {
                    left.invoke(this)
                }
            }

            if (isLeftVisible && isRightVisible) {
                VerticalDraggableDivider(
                    onDrag = { leftWidth += it },
                    dividerModifier,
                    onDragStopped = {
                        leftWidth = leftWidth.coerceIn(range)
                    }
                )
            }

            AnimatedVisibility(
                isRightVisible,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it },
            ) {
                Column(Modifier.fillMaxWidth()) {
                    right.invoke(this)
                }
            }
        }
    }
}