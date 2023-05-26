package org.solvo.web.ui.foundation

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
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
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.cursorHoverIcon


@Composable
fun IconOnDivider(
    width: Dp,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
    divider: @Composable () -> Unit,
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
) {
    val density = LocalDensity.current
    val dragState = rememberDraggableState { onDrag(with(density) { it.toDp() }) }

    IconOnDivider(
        width, Icons.Default.MoreVert,
        colorFilter = ColorFilter.tint(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                .compositeOver(backgroundColor)
        )
    ) {
        Divider(
            modifier
                .width(width)
                .draggable(dragState, Orientation.Horizontal)
                .cursorHoverIcon(CursorIcon.COL_RESIZE),
            color = backgroundColor
        )
    }
}