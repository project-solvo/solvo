package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.solvo.model.api.communication.*
import org.solvo.model.foundation.Uuid

class CommentRequests(
    override val client: Client,
) : Requests() {

    suspend fun getComment(
        coid: Uuid,
    ): CommentDownstream? = client.http.get(api("comments/$coid")).bodyOrNull()

    suspend fun deleteComment(
        coid: Uuid,
    ): Boolean = client.http.delete(api("comments/$coid")).status.isSuccess()

    suspend fun post(
        parentCoid: Uuid,
        upstream: CommentUpstream,
        kind: CommentKind,
    ): Uuid? = postCommentImpl(parentCoid, upstream, kind = kind)

    private suspend fun postCommentImpl(
        parentCoid: Uuid,
        upstream: CommentUpstream,
        kind: CommentKind,
    ): Uuid? {
        val url = when (kind) {
            CommentKind.ANSWER -> api("comments/$parentCoid/answer")
            CommentKind.THOUGHT -> api("comments/$parentCoid/thought")
            CommentKind.COMMENT -> api("comments/$parentCoid/comment")
        }
        return client.http.postAuthorized(url) {
            contentType(ContentType.Application.Json)
            setBody(upstream)
        }.bodyOrNull()
    }

    suspend fun getReactions(coid: Uuid): List<Reaction> {
        return client.http.getAuthorized(api("comments/${coid}/reactions")).body<List<Reaction>>()
    }

    suspend fun removeReaction(
        coid: Uuid,
        reactionKind: ReactionKind,
    ) {
        client.http.deleteAuthorized(api("comments/${coid}/reactions/${reactionKind.ordinal}"))
    }

    suspend fun addReaction(
        coid: Uuid,
        reactionKind: ReactionKind,
    ) {
        client.http.postAuthorized(api("comments/${coid}/reactions/new")) {
            contentType(ContentType.Application.Json)
            setBody(reactionKind)
        }
    }
}