package org.solvo.web

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.LightComment
import org.solvo.model.foundation.Uuid
import org.solvo.web.comments.CommentCard
import org.solvo.web.document.LocalSolvoWindow
import org.solvo.web.document.SolvoWindow
import org.solvo.web.editor.RichText
import org.solvo.web.ui.SolvoTopAppBar
import org.solvo.web.ui.VerticalDraggableDivider


fun main() {
    onWasmReady {
        SolvoWindow {
            SolvoTopAppBar()
            ArticlePageContent()
        }
    }
}

@Composable
private fun ArticlePageContent(

) {
    Row(Modifier.fillMaxSize()) {
        val window = LocalSolvoWindow.current
        val windowSize by window.size.collectAsState()

        var leftWidth by remember { mutableStateOf(windowSize.width * (1.0f - 0.618f)) }
        Box(Modifier.width(leftWidth)) {
            PaperView(
                courseTitle = { PaperTitle("Models of Computation", "2022", "1a") },
                onClickExpand = { },
                {
                    Image(Icons.Default.Newspaper, "Paper", Modifier.fillMaxSize())
                },
                Modifier.fillMaxSize()
            )
        }

        VerticalDraggableDivider(
            onDrag = { leftWidth += it },
            Modifier.fillMaxHeight(),
        )

        Column {
            ControlBar(Modifier.fillMaxWidth()) {

            }

            Box(Modifier.padding(vertical = 12.dp).padding(end = 12.dp, start = 12.dp).fillMaxSize()) {
                AnswersList()
            }
        }
    }
}

@Composable
private fun AnswersList() {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        repeat(2) {
            // TODO: 2023/5/25 view model 
            CommentCard(
                listOf(
                    LightComment(Uuid.random(), "评论", "", "我说中文"),
                    LightComment(Uuid.random(), "Commenter2", "", "[Image] Content 2"),
                ),
                Modifier.weight(1.0f, fill = true)
            ) {
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

@Composable
private fun PaperTitle(
    courseName: String,
    year: String,
    questionNumber: String,
) {
    Text(courseName, fontWeight = FontWeight.W800, fontSize = 22.sp)
    Text(year, Modifier.padding(start = 8.dp), fontWeight = FontWeight.W700, fontSize = 18.sp)
    Text(questionNumber, Modifier.padding(start = 8.dp), fontWeight = FontWeight.W600, fontSize = 16.sp)
}

@Composable
private fun PaperView(
    courseTitle: @Composable RowScope. () -> Unit,
    onClickExpand: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
) {
    Column(modifier) {
        ControlBar(Modifier.fillMaxWidth()) {
            courseTitle()
            Spacer(Modifier.weight(1f))
            IconButton(onClickExpand) {
                if (isExpanded) {
                    Icon(Icons.Default.Compress, "Compress")
                } else {
                    Icon(Icons.Default.Expand, "Expand")
                }
            }
        }

        Card(Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
private fun ControlBar(
    modifier: Modifier = Modifier,
    icons: @Composable RowScope.() -> Unit,
) {
    Row(
        Modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier.padding(horizontal = 12.dp, vertical = 8.dp).height(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icons()
        }
    }
}