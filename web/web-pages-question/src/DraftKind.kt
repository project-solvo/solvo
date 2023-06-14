package org.solvo.web

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.CommentKind

@Immutable
sealed interface DraftKind {
    val displayName: String
    val displayNameInPostButton: String get() = "Post $displayName"
    val icon: ImageVector

    sealed interface New : DraftKind

    data object Answer : New {
        override val displayName: String get() = "Answer"
        override val icon: ImageVector get() = Icons.Outlined.PostAdd
    }

    data object Thought : New {
        override val displayName: String
            get() = "Thought"
        override val icon: ImageVector
            get() = Icons.Outlined.TipsAndUpdates

    }

    data class Edit(
        val comment: CommentDownstream,
    ) : DraftKind {
        val originKind get() = comment.kind.toDraftKind()

        override val displayName: String get() = originKind.displayName
        override val displayNameInPostButton: String
            get() = "Confirm Edit"
        override val icon: ImageVector get() = originKind.icon
    }
}

val DraftKind.isNew get() = this is DraftKind.New

@Stable
fun CommentKind.toDraftKind(): DraftKind {
    return when (this) {
        CommentKind.COMMENT -> throw IllegalArgumentException("Invalid: $this")
        CommentKind.ANSWER -> DraftKind.Answer
        CommentKind.THOUGHT -> DraftKind.Thought
    }
}

@Stable
fun DraftKind.toCommentKind(): CommentKind? = when (this) {
    DraftKind.Answer -> CommentKind.ANSWER
    DraftKind.Thought -> CommentKind.THOUGHT
    is DraftKind.Edit -> null
}