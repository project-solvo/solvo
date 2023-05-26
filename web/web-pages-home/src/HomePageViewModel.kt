package org.solvo.web

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.solvo.model.Course
import org.solvo.web.requests.client
import org.solvo.web.viewModel.AbstractViewModel
import org.solvo.web.viewModel.launchInBackground


class HomePageViewModel : AbstractViewModel() {
    val courses: MutableState<List<Course>?> = mutableStateOf(null)

    suspend fun refreshCourses() {
        val courses = client.courses.getAllCourses()
        this.courses.value = courses
    }

    override fun init() {
        launchInBackground { refreshCourses() }
    }
}