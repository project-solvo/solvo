package org.solvo.web.ui.foundation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

operator fun Offset.get(orientation: Orientation): Float = when (orientation) {
    Orientation.Vertical -> y
    Orientation.Horizontal -> x
}

@Stable
fun Size.asIntSize(): IntSize = IntSize(this.width.roundToInt(), this.height.roundToInt())

@Stable
fun Size.asDpSize(): DpSize = DpSize(this.width.dp, this.height.dp)