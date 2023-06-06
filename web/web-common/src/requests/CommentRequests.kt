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
    ): CommentDownstream? = postCommentImpl(parentCoid, upstream, isAnswer = false)

    suspend fun postAnswer(
        parentCoid: Uuid,
        upstream: CommentUpstream,
    ): CommentDownstream? = postCommentImpl(parentCoid, upstream, isAnswer = true)

    private suspend fun postCommentImpl(
        parentCoid: Uuid,
        upstream: CommentUpstream,
        isAnswer: Boolean,
    ): CommentDownstream? {
        val url = if (isAnswer) {
            api("comment/post/$parentCoid/asAnswer")
        } else {
            api("comment/post/$parentCoid")
        }
        return client.http.post(url) {
            accountAuthorization()
            contentType(ContentType.Application.Json)
            setBody(upstream)
        }.bodyOrNull()
    }

}