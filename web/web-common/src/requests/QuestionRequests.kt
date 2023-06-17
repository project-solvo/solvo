package org.solvo.web.requests

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import org.solvo.model.api.communication.QuestionDownstream
import org.solvo.model.api.communication.QuestionEditRequest
import org.solvo.model.api.events.Event

class QuestionRequests(
    override val client: Client
) : Requests() {
    suspend fun getQuestion(
        courseCode: String,
        articleCode: String,
        questionCode: String,
    ): QuestionDownstream? =
        http.get("${apiUrl}/courses/$courseCode/articles/$articleCode/questions/$questionCode").bodyOrNull()

    suspend fun isQuestionExist(
        courseCode: String,
        articleCode: String,
        questionCode: String,
    ): Boolean {
        if (questionCode.isEmpty()) {
            return false
        }
        return http.head("${apiUrl}/courses/$courseCode/articles/$articleCode/questions/$questionCode").status.isSuccess()
    }

    suspend fun updateQuestion(
        courseCode: String,
        articleCode: String,
        questionCode: String,
        question: QuestionEditRequest,
    ) {
        http.patchAuthorized("${apiUrl}/courses/$courseCode/articles/$articleCode/questions/$questionCode") {
            contentType(ContentType.Application.Json)
            setBody(question)
        }
    }

    fun subscribeEvents(
        scope: CoroutineScope,
        courseCode: String,
        articleCode: String,
        questionCode: String,
    ): SharedFlow<Event> {
        return connectEvents(
            scope,
            "${apiUrl}/courses/$courseCode/articles/$articleCode/questions/$questionCode/events"
        )
    }
}