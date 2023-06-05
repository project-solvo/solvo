package org.solvo.web.requests

import io.ktor.client.request.*
import org.solvo.model.CommentDownstream
import org.solvo.model.foundation.Uuid

class CommentRequests(
    override val client: Client,
) : Requests() {

    suspend fun getComment(
        coid: Uuid,
    ): CommentDownstream? = client.http.get(api("comment/get/$coid")).bodyOrNull()

}