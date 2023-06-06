package org.solvo.web.comments

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.CommentDownstream
import org.solvo.model.foundation.Uuid
import org.solvo.web.dummy.createDummyText
import org.solvo.web.ui.SolvoWindow


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
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        for (item in items) {
            val state: CommentCardState = remember { CommentCardState() }

            var hasOverflow by remember { mutableStateOf(false) }

            CommentSummaryCard(
                state,
                Modifier.wrapContentHeight().fillMaxWidth(),
                showMoreSwitch = if (hasOverflow || state.showingMore.value) {
                    {
                        ShowMoreSwitch(it)
                    }
                } else null
            ) { backgroundColor ->
                CommentCardContent(
                    item,
                    backgroundColor,
                    when {
                        state.showingMore.value -> Modifier.wrapContentHeight()
                        hasOverflow -> Modifier.heightIn(max = 200.dp)
                        else -> Modifier.heightIn(max = 200.dp)
                    },
                    onLayout = { hasOverflow = hasVisualOverflow }
                )
            }
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
