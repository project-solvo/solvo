package org.solvo.web.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.solvo.model.User
import org.solvo.web.ui.foundation.wrapClearFocus
import org.solvo.web.ui.image.RoundedUserAvatar
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon
import org.solvo.web.utils.DateFormatter

@Stable
val CommentCardShape = RoundedCornerShape(16.dp)

@Composable
fun LargeCommentCard(
    author: User?,
    date: String,
    modifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
    onClickExpand: () -> Unit = {},
    isExpand: Boolean = false,
    subComments: @Composable (() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    val state: ShowMoreButtonState = remember { ShowMoreButtonState() }
    CommentCard(
        paddings = CommentCardPaddings.Large,
        state = state,
        modifier = modifier,
        authorLine = {
            AuthorLine(
                icon = {
                    RoundedUserAvatar(author?.avatarUrl, 48.dp)
                },
                authorName = {
                    Text(author?.username ?: "Anonymous")
                },
                date = {
                    Text(date)
                },
            ) {
                actions?.invoke()
                IconButton(onClick = wrapClearFocus(onClickExpand)) {
                    if (isExpand) {
                        Icon(Icons.Filled.CloseFullscreen, null)
                    } else {
                        Icon(Icons.Filled.OpenInFull, null)
                    }
                }
            }
        },
        onClickCard = onClickCard,
        showMoreSwitch = null,
        subComments = subComments,
        content = content,
    )
}

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

@Composable
fun CommentSummaryCard(
    viewModel: FullCommentCardViewModel,
    state: ShowMoreButtonState = remember { ShowMoreButtonState() },
    modifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
    showMoreSwitch: (@Composable (state: ShowMoreButtonState) -> Unit)? = null,
    content: @Composable (ColumnScope.(backgroundColor: Color) -> Unit),
) {
    CommentCard(
        paddings = CommentCardPaddings.Small,
        state = state,
        modifier = modifier,
        authorLine = {
            val postTime by viewModel.postTime.collectAsState(null)
            val author by viewModel.author.collectAsState(null)
            AuthorLineThin(
                icon = {
                    RoundedUserAvatar(author?.avatarUrl, 20.dp)
                },
                authorName = author?.username ?: "",
                date = postTime?.let { DateFormatter.format(it) } ?: "",
            )
        },
        onClickCard = onClickCard,
        showMoreSwitch = showMoreSwitch,
        content = content,
    )
}

@Composable
fun DraftCommentCard(
    modifier: Modifier = Modifier,
    state: ShowMoreButtonState = remember { ShowMoreButtonState() },
    onClickCard: () -> Unit = {},
    showMoreSwitch: (@Composable (state: ShowMoreButtonState) -> Unit)? = null,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    CommentCard(
        paddings = CommentCardPaddings.Small,
        state = state,
        modifier = modifier,
        authorLine = null,
        onClickCard = onClickCard,
        showMoreSwitch = showMoreSwitch,
        subComments = null,
        content = content,
    )
}

@Suppress("NAME_SHADOWING")
@Composable
internal fun CommentCard(
    paddings: CommentCardPaddings,
    state: ShowMoreButtonState,
    modifier: Modifier = Modifier,
    authorLine: (@Composable () -> Unit)? = null,
    onClickCard: () -> Unit = {},
    showMoreSwitch: (@Composable (state: ShowMoreButtonState) -> Unit)? = null,
    subComments: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    val backgroundColor = commentCardBackgroundColor()
    val onClickCard by rememberUpdatedState(onClickCard)
    val interactionBarViewModel = remember { InteractionBarViewModel(listOf(1, 5)) }

    Card(
        modifier.clickable(indication = null, onClick = onClickCard),
        shape = CommentCardShape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(Modifier.padding(top = paddings.top).padding(paddings.headLinePadding)) {
            authorLine?.invoke()
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

        if (subComments == null) {
            Spacer(Modifier.height(paddings.bottom).fillMaxWidth())
        }

        InteractionBar(interactionBarViewModel)
    }
}

@Composable
fun ShowMoreSwitch(state: ShowMoreButtonState) {
    Text(
        text = state.text.value,
        modifier = Modifier.clickable { state.switchSeeMore() },
        textDecoration = TextDecoration.Underline,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
    )
}


@Composable
private fun commentCardBackgroundColor() = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
