package org.solvo.web.requests

import io.ktor.client.request.*
import io.ktor.http.*
import org.solvo.model.api.communication.ArticleDownstream

class ArticleRequests(
    override val client: Client
) : Requests() {
    suspend fun getArticle(
        courseCode: String,
        articleCode: String
    ): ArticleDownstream? = http.get("${apiUrl}/courses/$courseCode/articles/$articleCode").bodyOrNull()


    suspend fun isArticleExist(
        courseCode: String,
        articleCode: String
    ): Boolean = http.head("${apiUrl}/courses/$courseCode/articles/$articleCode").status.isSuccess()


}