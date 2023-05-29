package org.solvo.web.ui.foundation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.geometry.Offset

operator fun Offset.get(orientation: Orientation): Float = when (orientation) {
    Orientation.Vertical -> y
    Orientation.Horizontal -> x
}