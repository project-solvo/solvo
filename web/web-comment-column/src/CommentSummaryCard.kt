package org.solvo.web.comments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.FullCommentDownstream
import org.solvo.web.editor.RichText
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.theme.UNICODE_FONT

fun main() {
    onWasmReady {
        SolvoWindow {
            repeat(2) {
                CommentSummaryCard(Modifier.weight(1.0f, fill = true)) {
                    RichText(
                        """
                                Some Java code
                                ```java
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                class X {}
                                ```
                            """.trimIndent(),
                        modifier = Modifier.weight(1.0f).fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun CommentSummaryCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
    ) {
    val shape = RoundedCornerShape(16.dp)
    var seeMore by remember { mutableStateOf(false) }
    if (!seeMore) {
        Card(shape = shape, modifier = modifier) {
            Author(
                icon = {
                AvatarBox(Modifier.size(48.dp)) {
                    Image(
                        Icons.Default.Person4,
                        "Avatar",
                        Modifier.matchParentSize(),
                    )
                }
            },
                authorName = {
                    Text("Alex")
                },
                Modifier.padding(horizontal = 16.dp).padding(top = 16.dp))

            Column(Modifier.padding(horizontal = 16.dp).padding(top = 12.dp).weight(1f)) {
                content()
            }

            Column(Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
                Text(
                    text = "See more",
                    modifier = Modifier.clickable { seeMore = true },
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    } else {

    }
}

@Composable
private fun Author(
    icon: @Composable BoxScope.() -> Unit,
    authorName: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.height(48.dp), verticalAlignment = Alignment.CenterVertically ) {
        Box(Modifier.size(48.dp)) {
            icon.invoke(this)
        }
        Spacer(Modifier.width(12.dp)) // 8.dp
        Box {
            ProvideTextStyle(AuthorNameTextStyle) {
                authorName()
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

@Composable
private fun AvatarBox(
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