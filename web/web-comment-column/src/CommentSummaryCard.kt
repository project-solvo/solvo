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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import org.solvo.model.CommentDownstream
import org.solvo.model.foundation.Uuid


fun main() {
    val commentDownstream1 = CommentDownstream(Uuid.random(), null, "Some java code", true, 123u,
        123u, Uuid.random(), true, listOf())
    val commentDownstream2 = CommentDownstream(Uuid.random(), null, "Some C code", true, 123u,
        123u, Uuid.random(), true, listOf())
    onWasmReady {
        SolvoWindow {
                CommentColumn(Modifier, listOf(commentDownstream1, commentDownstream2, commentDownstream2, commentDownstream1))
        }
    }
}

@Composable
fun CommentSummaryCard(
    modifier: Modifier = Modifier,
    commentDownstream: CommentDownstream,
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
                RichText(commentDownstream.content.trimIndent(), modifier = Modifier.fillMaxWidth())
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
        // TODO: to shrink by clicking "show less"
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


@Composable
fun CommentColumn(
    modifier: Modifier = Modifier,
    cards: List<CommentDownstream>,
) {
    Column(modifier = modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        for(card in cards) {
            Column(modifier = Modifier) {
                CommentSummaryCard(Modifier.height(200.dp).fillMaxWidth(), card)
            }
            Column(modifier = Modifier.height(10.dp).fillMaxWidth()){}
        }
    }
}