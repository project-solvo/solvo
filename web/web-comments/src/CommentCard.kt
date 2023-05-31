package org.solvo.web.comments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.solvo.model.User
import org.solvo.web.ui.modifiers.clickable

@Stable
val CommentCardShape = RoundedCornerShape(16.dp)

@Suppress("NAME_SHADOWING")
@Composable
fun CommentCard(
    author: User?,
    date: String,
    modifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
    subComments: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    val backgroundColor = commentCardBackgroundColor()
    val onClickCard by rememberUpdatedState(onClickCard)

    Card(
        modifier.clickable(indication = null, onClick = onClickCard),
        shape = CommentCardShape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        AuthorLine(
            icon = {
                AvatarBox(Modifier.size(48.dp)) {
                    Image(
                        // TODO: 2023/5/29 avatar 
                        Icons.Default.Person4,
                        "Avatar",
                        Modifier.matchParentSize(),
                    )
                }
            },
            authorName = {
                Text(author?.username ?: "Anonymous")
            },
            date = {
                Text(date)
            },
            Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
        )

        // content
        Column(Modifier.padding(horizontal = 16.dp).padding(top = 12.dp)) {
            content(backgroundColor)
        }

        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                .background(Color(0x212121), shape = CommentCardShape)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            subComments?.invoke()
        }
    }
}


@Composable
private fun commentCardBackgroundColor() = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
