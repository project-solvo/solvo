package org.solvo.web.answer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.LightCommentDownstream
import org.solvo.web.comments.commentCard.components.AuthorLineDateTextStyle
import org.solvo.web.comments.subComments.SubComments

@Composable
fun AnswerCardPreviewComments(
    item: CommentDownstream,
    onExpandAnswerUpdated: ((comment: LightCommentDownstream?, item: CommentDownstream) -> Unit)?
) {
    ProvideTextStyle(TextStyle(fontSize = AuthorLineDateTextStyle.fontSize)) {
        SubComments(
            item.previewSubComments,
            item.allSubCommentIds.size,
            Modifier.padding(top = 4.dp)
        ) {
            onExpandAnswerUpdated?.invoke(it, item)
        }
    }
}
