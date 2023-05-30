package org.solvo.web

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.*
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.foundation.Uuid
import org.solvo.web.comments.CommentCard
import org.solvo.web.comments.CourseMenu
import org.solvo.web.comments.CourseMenuState
import org.solvo.web.document.History
import org.solvo.web.document.parameters.article
import org.solvo.web.document.parameters.course
import org.solvo.web.document.parameters.question
import org.solvo.web.document.parameters.rememberPathParameters
import org.solvo.web.dummy.createDummyText
import org.solvo.web.editor.RichText
import org.solvo.web.ui.LoadableContent
import org.solvo.web.ui.LocalSolvoWindow
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar
import org.solvo.web.ui.foundation.VerticalDraggableDivider
import org.solvo.web.ui.image.rememberImagePainter
import kotlin.random.Random
import kotlin.random.nextUInt


fun main() {
    onWasmReady {
        SolvoWindow {
            val pathParameters = rememberPathParameters(WebPagePathPatterns.question)
            val course by pathParameters.course().collectAsState(null)
            val article by pathParameters.article().collectAsState(null)
            val question by pathParameters.question().collectAsState(null)

            val model = remember { ArticlePageViewModel() }

            val menuState = remember { CourseMenuState() }
            SolvoTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { menuState.switchMenuOpen() }) {
                        Icon(Icons.Filled.Menu, null)
                    }
                }
            ) {
                course?.let { article?.let { it1 -> PaperTitle(it, it1.name) } }
            }

            CourseMenu(
                menuState,
                model.allArticles,
                onClickQuestion = { articles: ArticleDownstream, questions: QuestionDownstream ->
                    History.navigate {
                        question(
                            articles.course.code,
                            articles.name,
                            questions.code
                        )
                    } // TODO: 2023/5/29 navigate article
                })


            LoadableContent(course == null || article == null || question == null, Modifier.fillMaxSize()) {
                ArticlePageContent(
                    course = course ?: return@LoadableContent,
                    article = article ?: return@LoadableContent,
                    question = question ?: return@LoadableContent
                )
            }
        }
    }
}

@Composable
private fun ArticlePageContent(
    course: Course,
    article: ArticleDownstream,
    question: QuestionDownstream,
) {
    Row(Modifier.fillMaxSize()) {
        val window = LocalSolvoWindow.current
        val windowSize by window.size.collectAsState()

        var leftWidth by remember { mutableStateOf(windowSize.width * (1.0f - 0.618f)) }
        Box(Modifier.width(leftWidth)) {
            PaperView(
                questionSelectedBar = {
                    // ScrollableTab row TODO()
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        article.questionIndexes.forEach {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(it)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                shape = RoundedCornerShape(8.dp),
                            )
                        }
                    }
                },
                onChangeLayout = {
                    // TODO change article page layout
                },
                Modifier.fillMaxSize()
            ) {
                Image(
                    rememberImagePainter(
                        "https://him188.github.io/static/images/WACCLangSpec_00.png",
                        error = Icons.Outlined.BrokenImage,
                    ),
                    "Article Content",
                    Modifier.fillMaxSize(),
                )
//                val painter by rememberUpdatedState(
//                    rememberImagePainter(
//                        "https://him188.github.io/static/images/WACCLangSpec_00.png",
//                        error = Icons.Outlined.BrokenImage,
//                    )
//                )
//                OverlayLoadableContent(isLoading = painter == NoOpPainter, Modifier.fillMaxSize()) {
//                    Image(
//                        painter,
//                        "Article Content",
//                        Modifier.fillMaxSize(),
//                    )
//                }
            }
        }

        VerticalDraggableDivider(
            onDrag = { leftWidth += it },
            Modifier.fillMaxHeight(),
        )

        Column {
            val pagingState = rememberPagingState(
                remember { generateSequence { createCommentDownstream() }.take(10).toList() },
                3
            )
            PagingContent(
                pagingState,
                controlBar = {
                    PagingControlBar(it) {
                        FilledTonalButton(
                            onClick = {},
                            Modifier.align(Alignment.CenterStart),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = buttonContentPaddings,
                        ) {
                            Icon(Icons.Outlined.PostAdd, "Draft Answer", Modifier.fillMaxHeight())

                            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Draft Answer",
                                    Modifier.padding(start = 4.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W500,
                                )
                            }
                        }
                    }
                }
            ) { items ->
                Box(Modifier.padding(top = 12.dp).padding(end = 12.dp, start = 12.dp).fillMaxSize()) {
                    AnswersList(items, Modifier.fillMaxSize())
                }
            }
        }
    }
}

private var testCommentId = 0
private fun createCommentDownstream(): CommentDownstream {
    val id = testCommentId++
    return CommentDownstream(
        Uuid.random(),
        if (Random.nextBoolean()) {
            null
        } else {
            User(Uuid.random(), "User $id", null)
        },
        createDummyText(id),
        Random.nextBoolean(),
        Random.nextUInt(),
        Random.nextUInt(),
        Uuid.random(),
        false,
        listOf(
            LightCommentDownstream(User(id = Uuid.random(), "查尔斯", null), "你是好人！"),
            LightCommentDownstream(User(id = Uuid.random(), "Commenter2", null), "[Image] Content 2"),
        )
    )
}

@Composable
private fun AnswersList(
    items: List<CommentDownstream>,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            CommentCard(
                item.author,
                "May 05, 2023", // TODO: 2023/5/29 date
                item.subComments,
                Modifier.wrapContentHeight().fillMaxWidth(),
            ) { backgroundColor ->
                RichText(
                    item.content,
                    modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                    onTextUpdated = {
//                        editorReady = true
                    },
                    propagateScrollState = scrollState,
                    backgroundColor = backgroundColor,
                    showScrollbar = false
                )


//                var actualHeight by remember { mutableStateOf(Dp.Unspecified) }
//                var editorReady by remember { mutableStateOf(false) }
//                OverlayLoadableContent(
//                    !editorReady,
//                    Modifier.height(actualHeight.takeOrElse { 20.dp }.coerceAtLeast(20.dp)).fillMaxWidth(),
//                    loadingContent = { LinearProgressIndicator() },
//                ) {
//                    
//                }
            }
        }
    }
}

@Composable
private fun PaperTitle(
    course: Course,
    year: String,
) {
    Row {
        Text(course.code, fontWeight = FontWeight.W800, fontSize = 22.sp)
        Text(course.name, Modifier.padding(start = 4.dp), fontWeight = FontWeight.W800, fontSize = 22.sp)
        Text(year, Modifier.padding(start = 16.dp), fontWeight = FontWeight.W700, fontSize = 18.sp)
    }

}

@Composable
private fun PaperView(
    questionSelectedBar: @Composable RowScope. () -> Unit,
    onChangeLayout: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    content: @Composable () -> Unit,
) {
    Column(modifier) {
        ControlBar(Modifier.fillMaxWidth()) {
            Row(
                Modifier.weight(1f)
            ) {
                questionSelectedBar()
            }

            IconButton(onChangeLayout) {
                if (isExpanded) {
                    Icon(Icons.Default.Compress, "Compress")
                } else {
                    Icon(Icons.Default.Expand, "Expand")
                }
            }
        }

        Column(Modifier.fillMaxSize()) {
            content()
        }
    }
}
