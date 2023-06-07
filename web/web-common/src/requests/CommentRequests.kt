package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.solvo.model.api.communication.CommentDownstream
import org.solvo.model.api.communication.CommentUpstream
import org.solvo.model.Reaction
import org.solvo.model.ReactionKind
import org.solvo.model.foundation.Uuid

class CommentRequests(
    override val client: Client,
) : Requests() {

    suspend fun getComment(
        coid: Uuid,
    ): CommentDownstream? = client.http.get(api("comments/$coid")).bodyOrNull()

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
            api("comments/$parentCoid/answer")
        } else {
            api("comments/$parentCoid/comment")
        }
        return client.http.post(url) {
            accountAuthorization()
            contentType(ContentType.Application.Json)
            setBody(upstream)
        }.bodyOrNull()
    }

    suspend fun getReactions(coid: Uuid): List<Reaction> {
        return client.http.get(api("comments/${coid}/reactions")) {
            accountAuthorization()
        }.body<List<Reaction>>()
    }

    suspend fun removeReaction(
        coid: Uuid,
        reactionKind: ReactionKind,
    ) {
        client.http.delete(api("comments/${coid}/reactions/${reactionKind.ordinal}")) {
            accountAuthorization()
        }
    }

    suspend fun addReaction(
        coid: Uuid,
        reactionKind: ReactionKind,
    ) {
        client.http.post(api("comments/${coid}/reactions/new")) {
            accountAuthorization()
            contentType(ContentType.Application.Json)
            setBody(reactionKind)
        }
    }
}