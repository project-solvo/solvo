package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import org.solvo.model.ArticleDownstream
import org.solvo.model.Course

open class CourseRequests(
    override val client: Client,
) : Requests {
    suspend fun getAllCourses(): List<Course> = http.get("${apiUrl}/courses").body()
    suspend fun getCourse(code: String): Course? = http.get("${apiUrl}/courses/$code").bodyOrNull()
    suspend fun getAllArticles(course: String): List<ArticleDownstream>? =
        http.get("${apiUrl}/courses/$course/articles").bodyOrNull()
}