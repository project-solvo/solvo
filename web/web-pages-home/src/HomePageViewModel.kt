package org.solvo.web

import kotlinx.coroutines.flow.MutableStateFlow
import org.solvo.model.Course
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.launchInBackground


class HomePageViewModel : AbstractViewModel() {
    val courses: MutableStateFlow<List<Course>?> = MutableStateFlow(null)

    private suspend fun refreshCourses() {
        val courses = client.courses.getAllCourses()
        this.courses.value = courses
    }

    override fun init() {
        launchInBackground { refreshCourses() }
    }
}