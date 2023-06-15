package org.solvo.web.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import org.solvo.web.editor.impl.RichEditor
import org.solvo.web.editor.impl.RichEditorIdManager
import org.solvo.web.editor.impl.onEditorLoaded

val DEFAULT_RICH_EDITOR_FONT_SIZE = 18.sp

@Composable
fun rememberRichEditorState(
    isEditable: Boolean,
    contentPadding: Dp = Dp.Unspecified,
    showToolbar: Boolean = false,
    fontSize: TextUnit = DEFAULT_RICH_EDITOR_FONT_SIZE,
): RichEditorState {
    val density = LocalDensity.current
    return remember { RichEditorState(isEditable, density, contentPadding, showToolbar, fontSize) }
}

@JsName("createRichEditorState")
fun RichEditorState(
    isEditable: Boolean,
    density: Density,
    contentPadding: Dp = Dp.Unspecified,
    showToolbar: Boolean = false,
    fontSize: TextUnit = DEFAULT_RICH_EDITOR_FONT_SIZE,
): RichEditorState {
    return RichEditorState(
        RichEditor.create(
            RichEditorIdManager.nextId(),
            density,
            isEditable,
            showToolbar,
            fontSize,
            contentPadding,
        )
    )
}


@Stable
class RichEditorState internal constructor(
    internal val richEditor: RichEditor
) : RememberObserver by richEditor {
    val contentMarkdown get() = richEditor.contentMarkdown

    suspend fun setContentMarkdown(value: String) {
        richEditor.setContentMarkdown(value)
    }

    suspend fun clearContent() {
        return setContentMarkdown("")
    }

    suspend fun setPreviewMarkdownAndClip(value: String, onClip: (size: DpSize) -> Unit) {
        richEditor.onEditorLoaded {
            richEditor.expectEditorChange {
                richEditor.editor.setValue(value)
            }
            richEditor.resizeToWrapPreviewContent(onClip)
            richEditor.resizeToWrapPreviewContent(onClip) // fix layout bug, like redundant paddings
        }
    }

    /**
     * Preview HTML
     */
    val previewHtml: String get() = richEditor.editor.getPreviewedHTML() as String
}
