package org.solvo.web.requests

import io.ktor.client.request.*
import org.solvo.model.QuestionDownstream

class QuestionRequests(
    override val client: Client
) : Requests() {
    suspend fun getQuestion(
        courseCode: String,
        articleCode: String,
        questionCode: String,
    ): QuestionDownstream? =
        http.get("${apiUrl}/courses/$courseCode/articles/$articleCode/questions/$questionCode").bodyOrNull()
}