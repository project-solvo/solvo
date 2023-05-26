package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import org.solvo.model.Course

class CourseRequests(
    override val client: Client,
) : Requests {
    suspend fun getAllCourses(): List<Course> = http.get("${apiUrl}/courses").body<List<Course>>()
}