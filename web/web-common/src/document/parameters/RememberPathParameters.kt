package org.solvo.web.document.parameters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.Course
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.api.events.Event
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
fun PathParameters.course(): Flow<Course?> {
    return argument(WebPagePathPatterns.VAR_COURSE_CODE).map {
        client.courses.getCourse(it)
    }
}

@Stable
fun PathParameters.article(): Flow<ArticleDownstream?> {
    return combine(
        argument(WebPagePathPatterns.VAR_COURSE_CODE),
        argument(WebPagePathPatterns.VAR_ARTICLE_CODE)
    ) { courseCode, articleCode ->
        client.articles.getArticle(courseCode, articleCode)
    }
}

@Stable
fun PathParameters.question(): Flow<QuestionDownstream?> {
    return combine(
        argument(WebPagePathPatterns.VAR_COURSE_CODE),
        argument(WebPagePathPatterns.VAR_ARTICLE_CODE),
        argument(WebPagePathPatterns.VAR_QUESTION_CODE),
    ) { courseCode, articleCode, questionCode ->
        client.questions.getQuestion(courseCode, articleCode, questionCode)
    }
}

@Stable
fun PathParameters.questionEvents(scope: CoroutineScope): Flow<Event> {
    return combine(
        argument(WebPagePathPatterns.VAR_COURSE_CODE),
        argument(WebPagePathPatterns.VAR_ARTICLE_CODE),
        argument(WebPagePathPatterns.VAR_QUESTION_CODE),
    ) { courseCode, articleCode, questionCode ->
        client.questions.subscribeEvents(scope, courseCode, articleCode, questionCode)
    }.flatMapLatest { it }
}


@Stable
inline fun <reified E : Enum<E>> PathParameters.settingGroup(): Flow<E?> {
    val values = enumValues<E>()
    return argument(WebPagePathPatterns.VAR_SETTING_GROUP).map { settingGroup ->
        values.find { it.name.equals(settingGroup, ignoreCase = true) }
    }
}

