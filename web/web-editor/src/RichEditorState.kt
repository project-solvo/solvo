package org.solvo.web.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
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
    val editor: RichEditor = remember {
        RichEditor.create(
            RichEditorIdManager.nextId(), density,
            isEditable,
            showToolbar,
            fontSize,
            contentPadding,
        )
    }
    return remember(editor) { RichEditorState(editor) }
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
