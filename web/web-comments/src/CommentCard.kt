package org.solvo.web.comments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.solvo.model.User
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon

@Stable
val CommentCardShape = RoundedCornerShape(16.dp)

@Composable
fun LargeCommentCard(
    author: User?,
    date: String,
    modifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
    subComments: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    val state: CommentCardState = remember { CommentCardState(modifier) }
    CommentCard(
        authorLine = {
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
            )
        },
        paddings = CommentCardPaddings.Large,
        state = state,
        modifier = modifier,
        onClickCard = onClickCard,
        showMoreSwitch = null,
        subComments = subComments,
        content = content,
    )
}

@Immutable
class CommentCardPaddings(
    val headLinePadding: PaddingValues,
    val contentPadding: PaddingValues,
    val bottom: Dp,
) {
    companion object {
        @Stable
        val Large = CommentCardPaddings(
            headLinePadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp),
            bottom = 16.dp,
        )

        @Stable
        val Small = CommentCardPaddings(
            headLinePadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp),
            bottom = 12.dp,
        )
    }
}

@Composable
fun CommentSummaryCard(
    modifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
    content: @Composable() (ColumnScope.(backgroundColor: Color) -> Unit),
) {
    val state: CommentCardState = remember { CommentCardState(modifier) }
    CommentCard(
        authorLine = {
            AuthorLineThin(
                icon = {
                    AvatarBox(Modifier.size(20.dp)) {
                        Image(
                            Icons.Default.Person4,
                            "Avatar",
                            Modifier.matchParentSize(),
                        )
                    }
                },
                authorName = "Alex",// actual: commentDownstream.author
                date = state.date.value,
            )
        },
        paddings = CommentCardPaddings.Small,
        state = state,
        modifier = modifier,
        onClickCard = onClickCard,
        showMoreSwitch = { ShowMoreSwitch(it) },
        content = content,
    )
}

@Suppress("NAME_SHADOWING")
@Composable
internal fun CommentCard(
    authorLine: @Composable () -> Unit,
    paddings: CommentCardPaddings,
    state: CommentCardState,
    modifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
    showMoreSwitch: (@Composable (state: CommentCardState) -> Unit)? = null,
    subComments: (@Composable () -> Unit)? = null,
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
        Row(Modifier.padding(paddings.headLinePadding)) {
            authorLine()
        }

        // content
        Column(Modifier.padding(paddings.contentPadding)) {
            content(backgroundColor)
        }

        if (showMoreSwitch != null) {
            Column(
                Modifier.padding(horizontal = 16.dp).padding(top = 6.dp).padding(bottom = 6.dp)
                    .cursorHoverIcon(CursorIcon.POINTER)
            ) {
                showMoreSwitch.invoke(state)
            }
        }

        if (subComments != null) {
            Column(
                Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    .background(Color(0x212121), shape = CommentCardShape)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                subComments.invoke()
            }
        }

        if (subComments == null && showMoreSwitch == null) {
            Spacer(Modifier.height(16.dp).fillMaxWidth())
        }
    }
}

@Composable
fun ShowMoreSwitch(state: CommentCardState) {
    Text(
        text = state.text.value,
        modifier = Modifier.clickable {
            state.switchSeeMore()
            state.switchCardModifier()
            state.changeText()
        },
        textDecoration = TextDecoration.Underline,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
    )
}


@Composable
private fun commentCardBackgroundColor() = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
