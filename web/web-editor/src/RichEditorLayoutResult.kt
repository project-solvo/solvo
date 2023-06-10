package org.solvo.web.editor

import androidx.compose.ui.unit.IntSize

class RichEditorLayoutResult(
    /**
     * The amount of space required to paint this text fully.
     * This would be the size of the full text.
     */
    val intrinsicSize: IntSize,

    /**
     * The amount of space required to paint this text, possibly partially.
     * This would be the size of the painted *part* of the full text.
     */
    val size: IntSize,
) {
    /**
     * Returns true if the text is too wide and couldn't fit with given width.
     */
    val didOverflowWidth: Boolean
        get() = size.width < intrinsicSize.width

    /**
     * Returns true if the text is too tall and couldn't fit with given height.
     */
    val didOverflowHeight: Boolean
        get() = size.height < intrinsicSize.height

    /**
     * Returns true if either vertical overflow or horizontal overflow happens.
     */
    val hasVisualOverflow get() = didOverflowHeight || didOverflowWidth


    internal fun canReuse(
        intrinsicSize: IntSize,
        size: IntSize,
    ): Boolean = this.intrinsicSize == intrinsicSize && this.size == size
}