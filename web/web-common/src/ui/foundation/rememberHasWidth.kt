package org.solvo.web.ui.foundation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp

@Composable
fun hasWidth(width: Dp): Boolean {
    var has by remember { mutableStateOf(false) }
    BoxWithConstraints {
        SideEffect {
            has = maxWidth >= width
        }
    }
    return has
}