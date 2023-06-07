package org.solvo.web.comments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.Course
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.foundation.Uuid
import org.solvo.web.ui.SolvoWindow
import org.solvo.web.ui.foundation.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            val model = remember { CourseMenuState() }
            CourseMenuContent(model)
        }
    }
}

@Composable
fun CourseMenuContent(state: CourseMenuState) {
    SolvoTopAppBar {
        IconButton(onClick = { state.switchMenuOpen() }) {
            Icon(Icons.Filled.Menu, null)
        }
    }
    Box {
        // testing data
        val articles = remember {
            mutableListOf<ArticleDownstream>().apply {
                listOf(
                    ArticleDownstream(
                        Uuid.random(),
                        null,
                        "",
                        true,
                        0u,
                        0u,
                        "50001",
                        "50001",
                        Course("50001", "50001"),
                        "2020",
                        listOf("1a", "1b", "2a"),
                        listOf(),
                        0u,
                        0u,
                    )
                )
            }
        }
        // show left column menu
        CourseMenu(state)
    }
}


@Composable
fun CourseMenu(
    state: CourseMenuState,
    onClickArticle: ((article: ArticleDownstream) -> Unit)? = null,
    onClickQuestion: ((article: ArticleDownstream, question: QuestionDownstream) -> Unit)? = null,
) {
    AnimatedVisibility(
        state.menuOpen.value,
        enter = slideInHorizontally(),
        exit = slideOutHorizontally(targetOffsetX = { -it })
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .width(250.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(10.dp))
        ) {
            // construct list of past papers
            state.allArticles.value.forEach { article ->
                ElevatedCard(
                    onClick = {
                        state.onClickArticle(article)
                        if (onClickArticle != null) {
                            onClickArticle(article)
                        }
                    },
                    modifier = Modifier.padding(10.dp).width(200.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = article.code,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    AnimatedVisibility(
                        state.questionListOpen.value
                                && state.clickedArticle.value == article
                    ) {
                        // construct list of questions
                        Column(Modifier.wrapContentHeight()) {
                            // TODO: 2023/5/29 fetch questions
//                            article.questions.forEach { question ->
//                                Row(
//                                    modifier = Modifier.padding(10.dp).padding(start = 20.dp)
//                                        .height(60.dp).width(160.dp).clickable {
//                                            state.onClickQuestion(article, question)
//                                            if (onClickQuestion != null) {
//                                                onClickQuestion(article, question)
//                                            }
//                                        }
//                                        .background(
//                                            color = if (
//                                                state.questionIndex.value == question
//                                                && article == state.clickedArticle.value
//                                            ) MaterialTheme.colorScheme.primary
//                                            else MaterialTheme.colorScheme.secondary,
//                                            shape = RoundedCornerShape(8.dp)
//                                        ),
//                                ) {
//                                    Text(
//                                        text = question.content,
//                                        modifier = Modifier.padding(8.dp),
//                                        style = MaterialTheme.typography.titleLarge,
//                                    )
//                                }
//                            }
                        }
                    }
                }
            }
        }
    }

}