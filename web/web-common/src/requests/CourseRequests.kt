package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.Course

open class CourseRequests(
    override val client: Client,
) : Requests() {
    suspend fun getAllCourses(): List<Course> = http.get("${apiUrl}/courses").body()
    suspend fun getCourse(code: String): Course? = http.get("${apiUrl}/courses/$code").bodyOrNull()

    suspend fun isCourseExist(code: String): Boolean = http.head("${apiUrl}/courses/$code").status.isSuccess()
    suspend fun getAllArticles(courseCode: String): List<ArticleDownstream>? =
        http.get("${apiUrl}/courses/$courseCode/articles").bodyOrNull()
}