package org.solvo.web.requests

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.ArticleEditRequest
import org.solvo.model.api.events.ArticleSettingPageEvent

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
    ): Boolean {
        if (articleCode.isEmpty()) {
            return false
        }
        return http.head("${apiUrl}/courses/$courseCode/articles/$articleCode").status.isSuccess()
    }

    suspend fun addArticle(
        courseCode: String,
        articleCode: String,
    ): Boolean {
        return http.postAuthorized(api("courses/$courseCode/articles/$articleCode")).status.isSuccess()
    }

    suspend fun update(
        courseCode: String,
        articleCode: String,
        request: ArticleEditRequest,
    ) {
        http.patchAuthorized(api("courses/$courseCode/articles/$articleCode")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    fun subscribeEvents(
        scope: CoroutineScope,
        courseCode: String,
        articleCode: String,
    ): Flow<ArticleSettingPageEvent> {
        return connectEvents(
            scope,
            "${apiUrl}/courses/$courseCode/articles/$articleCode/events"
        ).filterIsInstance()
    }
}