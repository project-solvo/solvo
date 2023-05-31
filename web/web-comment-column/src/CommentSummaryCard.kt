package org.solvo.web.comments.column

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.CommentDownstream
import org.solvo.model.foundation.Uuid
import org.solvo.web.comments.AuthorNameTextStyle
import org.solvo.web.comments.AvatarBox
import org.solvo.web.dummy.createDummyText
import org.solvo.web.editor.RichText
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.modifiers.clickable


fun main() {
    val context = createDummyText(123);

    val commentDownstream1 = CommentDownstream(
        Uuid.random(), null, context, true, 123u,
        123u, Uuid.random(), true, listOf()
    )
    val commentDownstream2 = CommentDownstream(
        Uuid.random(), null, context, true, 123u,
        123u, Uuid.random(), true, listOf()
    )
    onWasmReady {
        SolvoWindow {
            CommentColumn(
                Modifier,
                listOf(commentDownstream1, commentDownstream2)
            )
        }
    }
}

@Composable
fun CommentColumn(
    modifier: Modifier = Modifier,
    cards: List<CommentDownstream>,
) {
    Column(modifier = modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        for (card in cards) {
            Column(modifier = Modifier) {
                CommentSummaryCard(Modifier.height(200.dp).fillMaxWidth(), card)
            }
            Column(modifier = Modifier.height(10.dp).fillMaxWidth()) {}
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
    Card(shape = shape, modifier = (if (!seeMore) modifier else Modifier)) {
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
                Text("Alex") // actual: commentDownstream.author
            },
            Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
        )

        Column(Modifier.padding(horizontal = 16.dp).padding(top = 12.dp).then(if (seeMore) Modifier else Modifier.weight(1f))) {
            RichText(commentDownstream.content.trimIndent(), modifier = Modifier.fillMaxWidth())
        }

        Column(Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
            Text(
                text = (if (!seeMore) "See More" else "Show Less"),
                modifier = (if (!seeMore) Modifier.clickable { seeMore = true } else Modifier.clickable { seeMore = false }),
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun Author(
    icon: @Composable BoxScope.() -> Unit,
    authorName: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.height(48.dp), verticalAlignment = Alignment.CenterVertically) {
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
