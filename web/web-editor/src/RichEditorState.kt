package org.solvo.web.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import org.solvo.web.editor.impl.RichEditor
import org.solvo.web.editor.impl.RichEditorIdGenerator

@Composable
fun rememberRichEditorState(
): RichEditorState {
    val id: String = remember { RichEditorIdGenerator.nextId() }
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
    var contentMarkdown: String
        get() = richEditor.editor.getMarkdown() as String
        set(value) {
            richEditor.editor.setValue(value)
        }

    /**
     * Preview HTML
     */
    val previewHtml: String get() = richEditor.editor.getPreviewedHTML() as String

    var displayMode: RichEditorDisplayMode by richEditor::displayMode
    var isToolbarVisible: Boolean by richEditor::isToolbarVisible
    var isInDarkTheme: Boolean by richEditor::isInDarkTheme
}
