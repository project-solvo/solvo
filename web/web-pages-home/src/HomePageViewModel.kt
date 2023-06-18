package org.solvo.web

import kotlinx.coroutines.flow.*
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.Course
import org.solvo.web.document.parameters.PathParameters
import org.solvo.web.document.parameters.courseNullable
import org.solvo.web.event.withEvents
import org.solvo.web.requests.client
import org.solvo.web.settings.SettingGroup
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.launchInBackground


class HomePageViewModel : AbstractViewModel() {
    val courses: MutableStateFlow<List<Course>?> = MutableStateFlow(null)

    private val params = PathParameters(WebPagePathPatterns.course)

    val course = params.courseNullable().filterNotNull().shareInBackground()

    val courseCode: StateFlow<String> = course.map { it.code.str }.stateInBackground("")

    private val events = courseCode.flatMapLatest {
        client.courses.subscribeEvents(backgroundScope, it)
    }.shareInBackground()

    val articles: SharedFlow<List<ArticleDownstream>> =
        course.mapNotNull { client.courses.getAllArticles(it.code.str) }
            .stateInBackground(emptyList())
            .withEvents(events.filterIsInstance())
            .stateInBackground(emptyList())

    val settingGroups = courses.mapLatest { courses ->
        courses?.map { CourseSettingGroup(it.code.str, it.name) }?.plus(AddCourseGroup()) ?: listOf(AddCourseGroup())
    }.stateInBackground()

    val settingGroupName: StateFlow<String?> =
        params.argumentNullable(WebPagePathPatterns.VAR_COURSE_CODE)

    val selectedSettingGroup: StateFlow<SettingGroup<PageViewModel>?> = combine(
        settingGroups,
        settingGroupName,
    ) { list, code ->
        list?.find { it.pathName == code }
    }.stateInBackground()

    private suspend fun refreshCourses() {
        val courses = client.courses.getAllCourses()
        this.courses.value = courses
    }

    override fun init() {
        launchInBackground { refreshCourses() }
    }
}

