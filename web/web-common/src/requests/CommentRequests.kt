package org.solvo.web.requests

import io.ktor.client.request.*
import io.ktor.http.*
import org.solvo.model.CommentDownstream
import org.solvo.model.CommentUpstream
import org.solvo.model.foundation.Uuid

class CommentRequests(
    override val client: Client,
) : Requests() {

    suspend fun getComment(
        coid: Uuid,
    ): CommentDownstream? = client.http.get(api("comment/get/$coid")).bodyOrNull()

    suspend fun postComment(
        parentCoid: Uuid,
        upstream: CommentUpstream,
    ): CommentDownstream? = client.http.post(api("comment/post/$parentCoid")) {
        accountAuthorization()
        contentType(ContentType.Application.Json)
        setBody(upstream)
    }.bodyOrNull()

}