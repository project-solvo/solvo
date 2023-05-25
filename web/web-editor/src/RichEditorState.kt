package org.solvo.web.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import org.solvo.web.editor.impl.RichEditor
import org.solvo.web.editor.impl.RichEditorIdManager

@Composable
fun rememberRichEditorState(
): RichEditorState {
    val id: String = remember { RichEditorIdManager.nextId() }
    val editor: RichEditor = remember(id) { RichEditor.create(id) }
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
