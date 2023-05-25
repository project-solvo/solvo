package org.solvo.web.comments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.LightComment
import org.solvo.model.foundation.Uuid
import org.solvo.web.document.SolvoWindow
import org.solvo.web.editor.RichText
import org.solvo.web.ui.Rounded
import org.solvo.web.ui.SolvoTopAppBar
import org.solvo.web.ui.clickable
import org.solvo.web.ui.theme.UNICODE_FONT


@Composable
fun CommentCard(
    subComments: List<LightComment>,
    modifier: Modifier = Modifier,
    onClickCard: () -> Unit = {},
    onClickComment: ((LightComment?) -> Unit)? = null, // null if clicking "Show all"
    content: @Composable ColumnScope.() -> Unit,
) {
    val subCommentsState by rememberUpdatedState(subComments)
    val shape = RoundedCornerShape(8.dp)
    Card(modifier.clickable(indication = null, onClick = onClickCard), shape = shape) {
        Column(Modifier.padding(top = 4.dp).fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            // content
            Column(Modifier.padding(horizontal = 8.dp).weight(1f)) {
                content()
            }

            Divider(
                Modifier.padding(vertical = 4.dp).fillMaxWidth().height(1.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )

            val showComments by derivedStateOf { subCommentsState.take(3) }
            if (showComments.isNotEmpty()) {
                Column(
                    Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        .background(Color(0x212121), shape = shape)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (subComment in showComments) {
                        CommentLine(
                            subComment,
                            Modifier.clickable(indication = null) { onClickComment?.invoke(subComment) },
                        )
                    }

                    CommentLine(
                        Modifier.clickable(indication = null) { onClickComment?.invoke(null) },
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
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentLine(
    subComment: LightComment,
    modifier: Modifier = Modifier
) {
    CommentLine(
        modifier,
        icon = {
            Rounded {
                Image(
                    Icons.Default.Person, "Comment Author", Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current)
                )
            }
        },
        authorName = { Text(subComment.authorName) },
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
                ProvideTextStyle(
                    LocalTextStyle.current.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        fontFamily = UNICODE_FONT,
                    )
                ) {
                    authorName()
                }
            }
            Text(": ", fontWeight = FontWeight.Light)
        }

        message()
    }
}


fun main() {
    onWasmReady {
        SolvoWindow {
            SolvoTopAppBar()
            Box(Modifier.padding(32.dp).fillMaxSize().weight(0.6f)) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    repeat(3) {
                        CommentCard(
                            listOf(
                                LightComment(Uuid.random(), "评论", "", "我说中文"),
                                LightComment(Uuid.random(), "Commenter2", "", "[Image] Content 2"),
                            ),
                            Modifier.weight(1.0f, fill = true)
                        ) {
                            RichText(
                                """
                                ```math
                                \sqrt{x^2}
                                ```
                            """.trimIndent(),
                                modifier = Modifier.weight(1.0f).fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}