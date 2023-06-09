package org.solvo.web.requests

import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import org.solvo.model.api.communication.QuestionDownstream
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