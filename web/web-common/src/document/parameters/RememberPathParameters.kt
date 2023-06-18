package org.solvo.web.document.parameters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
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
fun PathParameters.courseNullable(): Flow<Course?> {
    return argumentNullable(WebPagePathPatterns.VAR_COURSE_CODE).filterNotNull().map {
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
fun PathParameters.question(paramName: String = WebPagePathPatterns.VAR_QUESTION_CODE): Flow<QuestionDownstream?> {
    return combine(
        argument(WebPagePathPatterns.VAR_COURSE_CODE),
        argument(WebPagePathPatterns.VAR_ARTICLE_CODE),
        argument(paramName),
    ) { courseCode, articleCode, questionCode ->
        client.questions.getQuestion(courseCode, articleCode, questionCode)
    }
}

@Stable
fun PathParameters.questionCode(): StateFlow<String?> {
    return argumentNullable(WebPagePathPatterns.VAR_QUESTION_CODE)
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
inline fun <reified E> PathParameters.settingGroup(noinline default: () -> E): Flow<E?> where E : Enum<E>, E : PathVariable =
    settingGroup(enumValues<E>().asList(), default)

@Stable
fun <E : PathVariable> PathParameters.settingGroup(entries: Collection<E>, default: () -> E): Flow<E> {
    return settingGroup(entries, { it.pathName }, default)
}

@Stable
fun <E> PathParameters.settingGroup(entries: Collection<E>, getPathName: (E) -> String, default: () -> E): Flow<E> {
    return argumentNullable(WebPagePathPatterns.VAR_SETTING_GROUP).map { settingGroup ->
        entries.find { getPathName(it).equals(settingGroup, ignoreCase = true) } ?: default()
    }
}

