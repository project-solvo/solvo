package org.solvo.web.comments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.solvo.model.LightCommentDownstream
import org.solvo.model.User
import org.solvo.web.ui.foundation.Rounded
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon
import org.solvo.web.ui.theme.UNICODE_FONT


@Composable
fun CommentCard(
    author: User?,
    date: String,
    subComments: List<LightCommentDownstream>,
    modifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
    onClickComment: ((LightCommentDownstream?) -> Unit)? = null, // null if clicking "Show all"
    content: @Composable ColumnScope.(backgroundColor: Color) -> Unit,
) {
    val subCommentsState by rememberUpdatedState(subComments)
    val shape = remember { RoundedCornerShape(16.dp) }
    val backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    Card(
        modifier.clickable(indication = null, onClick = onClickCard),
        shape = shape,
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

        val showComments by derivedStateOf { subCommentsState.take(3) }
        if (showComments.isNotEmpty()) {
            Column(
                Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    .background(Color(0x212121), shape = shape)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (subComment in showComments) {
                    CommentLine(
                        subComment,
                        Modifier.clickable(indication = null) { onClickComment?.invoke(subComment) },
                    )
                }

                CommentLine(
                    Modifier
                        .cursorHoverIcon(CursorIcon.POINTER)
                        .clickable(indication = null) { onClickComment?.invoke(null) },
                    icon = {
                        Image(
                            Icons.Outlined.Chat, "Show More Comments", Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        )
                    },
                    message = {
                        Text(
                            remember(subComments.size) { "See all ${subComments.size} comments" },
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun AvatarBox(
    modifier: Modifier = Modifier,
    image: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier
            .clip(CircleShape)
            .background(color = Color.Gray)
            .border(color = MaterialTheme.colorScheme.outline, width = 1.dp, shape = CircleShape)
    ) {
        image()
    }
}


@Composable
private fun AuthorLine(
    icon: @Composable BoxScope.() -> Unit,
    authorName: @Composable () -> Unit,
    date: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier.height(48.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp)) {
            icon.invoke(this)
        }

        Spacer(Modifier.width(12.dp)) // 8.dp
        Column {
            Row(Modifier.height(24.dp), verticalAlignment = Alignment.CenterVertically) {
                ProvideTextStyle(AuthorNameTextStyle) {
                    authorName()
                }
            }
            Row(Modifier.height(24.dp), verticalAlignment = Alignment.CenterVertically) {
                ProvideTextStyle(DateTextStyle) {
                    date()
                }
            }
        }
    }
}

val AuthorNameTextStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    fontFamily = UNICODE_FONT,
    textAlign = TextAlign.Center,
    lineHeight = 22.sp,
)

val DateTextStyle = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    fontFamily = UNICODE_FONT,
    textAlign = TextAlign.Center,
    lineHeight = 18.sp,
)

@Composable
private fun CommentLine(
    subComment: LightCommentDownstream,
    modifier: Modifier = Modifier
) {
    CommentLine(
        modifier,
        icon = {
            Rounded {
                AvatarBox(Modifier.size(24.dp)) {
                    Image(
                        Icons.Default.Person2,
                        "Avatar",
                        Modifier.matchParentSize(),
                    )
                }
            }
        },
        authorName = { Text(subComment.author?.username ?: "") }, // TODO: 2023/5/29 handle anonymous 
    ) {
        Text(subComment.content)
    }
}

@Composable
private fun CommentLine(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    authorName: (@Composable () -> Unit)? = null,
    message: @Composable RowScope.() -> Unit = {},
) {
    Row(modifier.height(24.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(24.dp)) {
            icon?.invoke()
        }

        Spacer(Modifier.width(8.dp))

        if (authorName != null) {
            Row(Modifier.height(24.dp), verticalAlignment = Alignment.CenterVertically) {
                ProvideTextStyle(AuthorNameTextStyle) {
                    authorName()
                }
            }
            Text(": ", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = UNICODE_FONT)
        }

        ProvideTextStyle(
            LocalTextStyle.current.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                fontFamily = UNICODE_FONT,
            )
        ) {
            message()
        }
    }
}
