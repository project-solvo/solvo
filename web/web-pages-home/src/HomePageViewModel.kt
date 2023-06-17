package org.solvo.web

import kotlinx.coroutines.flow.*
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.Course
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.document.parameters.courseNullable
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.launchInBackground


class HomePageViewModel : AbstractViewModel() {
    private val params = PathParameters(WebPagePathPatterns.course)
    val courses: MutableStateFlow<List<Course>?> = MutableStateFlow(null)


    val course = params.courseNullable().filterNotNull().shareInBackground()

    val articles: SharedFlow<List<ArticleDownstream>> =
        course.mapNotNull { client.courses.getAllArticles(it.code.str) }.shareInBackground()

    val courseCode: SharedFlow<String> = course.map { it.code.str }.shareInBackground()

    private suspend fun refreshCourses() {
        val courses = client.courses.getAllCourses()
        this.courses.value = courses
    }
    override fun init() {
        launchInBackground { refreshCourses() }
    }
}