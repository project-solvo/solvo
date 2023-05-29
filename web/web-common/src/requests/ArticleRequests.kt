package org.solvo.web.requests

import io.ktor.client.request.*
import org.solvo.model.ArticleDownstream

class ArticleRequests(
    override val client: Client
) : Requests {
    suspend fun getArticle(
        courseCode: String,
        articleCode: String
    ): ArticleDownstream? = http.get("${apiUrl}/courses/$courseCode/articles/$articleCode").bodyOrNull()
}