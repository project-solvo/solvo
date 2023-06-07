package org.solvo.web.comments.commentCard

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
class CommentCardPaddings(
    val top: Dp,
    val headLinePadding: PaddingValues,
    val contentPadding: PaddingValues,
    val bottom: Dp,
) {
    companion object {
        @Stable
        val Large = CommentCardPaddings(
            top = 16.dp,
            headLinePadding = PaddingValues(start = 16.dp, end = 16.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp),
            bottom = 16.dp,
        )

        @Stable
        val Small = CommentCardPaddings(
            top = 12.dp,
            headLinePadding = PaddingValues(start = 12.dp, end = 12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 6.dp),
            bottom = 12.dp,
        )
    }
}