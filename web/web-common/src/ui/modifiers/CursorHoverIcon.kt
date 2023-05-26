package org.solvo.web.ui.modifiers

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import kotlinx.browser.document
import org.w3c.dom.Document

fun Modifier.cursorHoverIcon(
    hoverIcon: CursorIcon,
    defaultIcon: CursorIcon = CursorIcon.AUTO,
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "cursorHoverIcon"
        properties["hoverIcon"] = hoverIcon
        properties["defaultIcon"] = defaultIcon
    }
) {
    val hoverInteractions = remember { MutableInteractionSource() }
    val isHovered by hoverInteractions.collectIsHoveredAsState()
    if (isHovered) {
        LaunchedEffect(true) {
            document.setCursorIcon(CursorIcon.COL_RESIZE)
        }
    } else {
        LaunchedEffect(true) {
            document.setCursorIcon(CursorIcon.AUTO)
        }
    }

    Modifier.hoverable(hoverInteractions)
}

// https://developer.mozilla.org/en-US/docs/Web/CSS/cursor
enum class CursorIcon(
    val value: String
) {
    AUTO("auto"),
    DEFAULT("default"),
    COL_RESIZE("col-resize"),
    POINTER("pointer"),
}

fun Document.setCursorIcon(cursorIcon: CursorIcon) {
    body.asDynamic().style.cursor = cursorIcon.value
}