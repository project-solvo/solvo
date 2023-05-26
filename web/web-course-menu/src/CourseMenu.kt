package org.solvo.web.comments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.skiko.wasm.onWasmReady
import org.solvo.model.Article
import org.solvo.model.Question
import org.solvo.web.document.SolvoWindow
import org.solvo.web.ui.SolvoTopAppBar

fun main() {
    onWasmReady {
        SolvoWindow {
            val model = remember { CourseState() }
            CourseMenuContent(model)
        }
    }
}

@Composable
fun CourseMenuContent(state: CourseState) {
    SolvoTopAppBar {
        IconButton(onClick = { state.switchMenuOpen() }) {
            Icon(Icons.Filled.Menu, null)
        }
    }
    Box {
        // testing data
        val articles = remember {
            mutableListOf<Article>().apply {
                for (i in 2018 until 2023) {
                    val questions =
                        listOf(
                            Question("1a"), Question("1b"),
                            Question("2a"), Question("2b")
                        )
                    this.add(Article(i.toString(), questions))
                }
            }
        }
        // show left column menu
        CourseMenu(state, state.menuOpen.value, articles)
    }
}


@Composable
fun CourseMenu(
    state: CourseState,
    isOpen: Boolean,
    articles: MutableList<Article>? = null,
    onClickArticle: ((article: Article) -> Unit)? = null,
    onClickQuestion: ((article: Article, question: Question) -> Unit)? = null,
) {
    AnimatedVisibility(
        isOpen,
        enter = slideInHorizontally(),
        exit = slideOutHorizontally(targetOffsetX = { -it })
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .width(250.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(10.dp))
        ) {
            // construct list of past papers
            articles?.forEach { article ->
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
                        text = article.termYear,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    AnimatedVisibility(
                        state.questionListOpen.value
                                && state.clickedArticle.value == article
                    ) {
                        // construct list of questions
                        Column(Modifier.wrapContentHeight()) {
                            article.questions.forEach { question ->
                                Row(
                                    modifier = Modifier.padding(10.dp).padding(start = 20.dp)
                                        .height(60.dp).width(160.dp).clickable {
                                            state.onClickQuestion(article, question)
                                            if (onClickQuestion != null) {
                                                onClickQuestion(article, question)
                                            }
                                        }
                                        .background(
                                            color = if (
                                                state.questionIndex.value == question
                                                && article == state.clickedArticle.value
                                            ) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.secondary,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                ) {
                                    Text(
                                        text = question.content,
                                        modifier = Modifier.padding(8.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}