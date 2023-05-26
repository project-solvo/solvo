package org.solvo.web.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import org.solvo.web.editor.impl.RichEditor
import org.solvo.web.editor.impl.RichEditorIdManager

val DEFAULT_RICH_EDITOR_FONT_SIZE = 18.sp

@Composable
fun rememberRichEditorState(): RichEditorState {
    val editor: RichEditor = remember { RichEditor.create(RichEditorIdManager.nextId()) }
    return remember(editor) { RichEditorState(editor) }
}


@Stable
class RichEditorState internal constructor(
    internal val richEditor: RichEditor
) {
    /**
     * Input markdown
     */
    val contentMarkdown: String
        get() = richEditor.editor.getMarkdown() as String

    suspend fun setContentMarkdown(value: String) {
        richEditor.onEditorLoaded {
            richEditor.expectEditorChange {
                richEditor.editor.setValue(value)
            }
        }
    }

    /**
     * Preview HTML
     */
    val previewHtml: String get() = richEditor.editor.getPreviewedHTML() as String
}
