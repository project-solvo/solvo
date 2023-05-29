package org.solvo.web.document.parameters

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.solvo.model.ArticleDownstream
import org.solvo.model.Course
import org.solvo.model.QuestionDownstream
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.web.requests.client

@Composable
fun rememberPathParameters(
    pattern: String,
): PathParameters {
    val parameters: PathParameters = remember(pattern) { PathParameters(pattern) }
    LaunchedEffect(pattern) {
        parameters.pattern = pattern
    }
    return parameters
}

@Stable
@Composable
fun PathParameters.course(): SharedFlow<Course?> {
    val code by param(WebPagePathPatterns.VAR_COURSE_CODE)
    val state: MutableSharedFlow<Course?> = remember { MutableSharedFlow() }
    LaunchedEffect(code) {
        state.emit(client.courses.getCourse(code))
    }
    return state
}

@Stable
@Composable
fun PathParameters.article(): SharedFlow<ArticleDownstream?> {
    val courseCode by param(WebPagePathPatterns.VAR_COURSE_CODE)
    val articleCode by param(WebPagePathPatterns.VAR_ARTICLE_CODE)
    val state: MutableSharedFlow<ArticleDownstream?> = remember { MutableSharedFlow() }
    LaunchedEffect(courseCode, articleCode) {
        state.emit(client.articles.getArticle(courseCode, articleCode))
    }
    return state
}

@Stable
@Composable
fun PathParameters.question(): SharedFlow<QuestionDownstream?> {
    val courseCode by param(WebPagePathPatterns.VAR_COURSE_CODE)
    val articleCode by param(WebPagePathPatterns.VAR_ARTICLE_CODE)
    val questionCode by param(WebPagePathPatterns.VAR_QUESTION_CODE)
    val state: MutableSharedFlow<QuestionDownstream?> = remember { MutableSharedFlow() }
    LaunchedEffect(courseCode, articleCode, questionCode) {
        state.emit(client.questions.getQuestion(courseCode, articleCode, questionCode))
    }
    return state
}