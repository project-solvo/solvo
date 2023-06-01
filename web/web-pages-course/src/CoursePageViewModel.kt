package org.solvo.web

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.solvo.model.ArticleDownstream
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.document.parameters.course
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel

class CoursePageViewModel : AbstractViewModel() {
    private val params = PathParameters(WebPagePathPatterns.course)

    val course = params.course().filterNotNull().shareInBackground()

    val articles: SharedFlow<List<ArticleDownstream>> =
        course.mapNotNull { client.courses.getAllArticles(it.code) }.shareInBackground()

    val courseCode: SharedFlow<String> = course.map { it.code }.shareInBackground()
}