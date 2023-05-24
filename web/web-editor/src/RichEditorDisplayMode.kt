package org.solvo.web.editor

import androidx.compose.runtime.Immutable

@Immutable
enum class RichEditorDisplayMode(
    internal val showEditor: Boolean,
    internal val showPreview: Boolean,
) {
    EDIT_ONLY(showEditor = true, showPreview = false),
    PREVIEW_ONLY(showEditor = false, showPreview = true),
    EDIT_PREVIEW(showEditor = true, showPreview = true),
    NONE(showEditor = true, showPreview = true),
}