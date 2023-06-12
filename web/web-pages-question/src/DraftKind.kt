package org.solvo.web

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.solvo.model.api.communication.CommentKind

@Immutable
enum class DraftKind(
    val displayName: String,
    val icon: ImageVector,
    val highlightColor: @Composable () -> Color,
) {
    ANSWER("Answer", Icons.Outlined.PostAdd, { MaterialTheme.colorScheme.primaryContainer }),
    THOUGHT("Thought", Icons.Outlined.TipsAndUpdates, { MaterialTheme.colorScheme.secondaryContainer }), ;
}

@Stable
fun CommentKind.toDraftKind(): DraftKind {
    return when (this) {
        CommentKind.COMMENT -> throw IllegalArgumentException("Invalid: $this")
        CommentKind.ANSWER -> DraftKind.ANSWER
        CommentKind.THOUGHT -> DraftKind.THOUGHT
    }
}

@Stable
fun DraftKind.toCommentKind(): CommentKind = when (this) {
    DraftKind.ANSWER -> CommentKind.ANSWER
    DraftKind.THOUGHT -> CommentKind.THOUGHT
}