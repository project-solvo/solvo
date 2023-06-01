package org.solvo.web.comments

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.CommentDownstream
import org.solvo.model.foundation.Uuid
import org.solvo.web.dummy.createDummyText
import org.solvo.web.editor.RichText
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.modifiers.CursorIcon
import org.solvo.web.ui.modifiers.clickable
import org.solvo.web.ui.modifiers.cursorHoverIcon


fun main() {
    val context = createDummyText(123);

    val commentDownstream1 = CommentDownstream(
        Uuid.random(), null, context, true, 123u,
        123u, Uuid.random(), true, 0, 0, 0, listOf(), listOf()
    )
    val commentDownstream2 = CommentDownstream(
        Uuid.random(), null, context, true, 123u,
        123u, Uuid.random(), true, 0, 0, 0, listOf(), listOf()
    )
    onWasmReady {
        SolvoWindow {
            CommentColumn(
                listOf(
                    commentDownstream1, commentDownstream2,
                    commentDownstream1, commentDownstream2, commentDownstream2
                ),
                Modifier
            )
        }
    }
}

@Composable
fun CommentColumn(
    items: List<CommentDownstream>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        for (item in items) {
            Column(modifier = Modifier) {
                CommentSummaryCard(Modifier.height(200.dp).fillMaxWidth()) { backgroundColor ->
                    CommentCardContent(item, backgroundColor, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CommentSummaryCard(
    modifier: Modifier = Modifier,
    commentDownstream: CommentDownstream,
) {
    val shape = RoundedCornerShape(16.dp)
    val state = remember { CommentCardState(modifier) }
    Card(shape = shape, modifier = state.currentCardModifier.value) {
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

        Column(
            Modifier.padding(horizontal = 16.dp).padding(top = 6.dp)
                .then(if (state.seeMore.value) Modifier else Modifier.weight(1f))
        ) {
            RichText(commentDownstream.content.trimIndent(), modifier = Modifier.fillMaxWidth())
        }

        Column(
            Modifier.padding(horizontal = 16.dp).padding(top = 6.dp).padding(bottom = 6.dp)
                .cursorHoverIcon(CursorIcon.POINTER)
        ) {
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
    }
}

@Composable
fun AuthorLineThin(
    icon: @Composable BoxScope.() -> Unit,
    authorName: String,
    date: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier.height(25.dp)) {
        Box(Modifier.size(30.dp)) {
            icon.invoke(this)
        }
        Box {
            ProvideTextStyle(AuthorNameTextStyle) {
                Text(authorName, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 3.dp))
            }
        }
        Box(modifier = Modifier.offset(y = -2.dp)) {
            Text("(last edited: " + date + ")", fontSize = 15.sp)
        }
    }
}
