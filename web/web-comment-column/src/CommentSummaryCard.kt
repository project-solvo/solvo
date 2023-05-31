package org.solvo.web.comments.column

import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.CommentDownstream
import org.solvo.model.foundation.Uuid
import org.solvo.web.comments.AuthorNameTextStyle
import org.solvo.web.comments.AvatarBox
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
        123u, Uuid.random(), true, 0, 0, 0, listOf()
    )
    val commentDownstream2 = CommentDownstream(
        Uuid.random(), null, context, true, 123u,
        123u, Uuid.random(), true, 0, 0, 0, listOf()
    )
    onWasmReady {
        SolvoWindow {
            CommentColumn(
                Modifier,
                listOf(commentDownstream1, commentDownstream2,
                    commentDownstream1, commentDownstream2, commentDownstream2)
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
    val model by remember {mutableStateOf( CommentCardState(modifier, commentDownstream))}
    Card(shape = shape, modifier = model.currentCardModifier.value) {
        Author(
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
            date = model.date.value,
            Modifier.padding(horizontal = 16.dp).padding(top = 10.dp),
        )

        Column(Modifier.padding(horizontal = 16.dp).padding(top = 6.dp).then(if (model.seeMore.value) Modifier else Modifier.weight(1f))) {
            RichText(commentDownstream.content.trimIndent(), modifier = Modifier.fillMaxWidth())
        }

        Column(Modifier.padding(horizontal = 16.dp).padding(top = 6.dp).padding(bottom = 6.dp)
            .cursorHoverIcon(CursorIcon.POINTER)) {
            Text(
                text = model.text.value,
                modifier = Modifier.clickable {
                    model.switchSeeMore()
                    model.switchCardModifier()
                    model.changeText()} ,
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
