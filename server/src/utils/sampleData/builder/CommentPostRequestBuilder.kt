package org.solvo.server.utils.sampleData.builder

import org.intellij.lang.annotations.Language
import org.solvo.model.api.communication.CommentUpstream
import org.solvo.model.utils.NonBlankString
import org.solvo.server.ServerContext
import org.solvo.server.utils.sampleData.SampleDataDslMarker
import java.util.*

class CommentPostRequest(
    val author: UserRegisterRequest,
    val content: () -> String,
    val anonymity: Boolean,
    val pinned: Boolean,
    val comments: List<CommentPostRequest> = listOf(),
    val isAnswer: Boolean = false,
) {
    suspend fun submit(
        db: ServerContext.Databases,
        parentId: UUID
    ) {
        db.contents.apply {
            val commentId = if (isAnswer) {
                postAnswer(
                    answer = CommentUpstream(NonBlankString.fromString(content()), anonymity),
                    authorId = author.uid,
                    questionId = parentId
                )!! // TODO: fix bug (possibly problem with contains())
            } else {
                postComment(
                    comment = CommentUpstream(NonBlankString.fromString(content()), anonymity),
                    authorId = author.uid,
                    parentId = parentId
                )!!
            }
            // TODO: pin the comment
            comments.map { commentRequest -> commentRequest.submit(db, commentId) }
        }
    }
}

@SampleDataDslMarker
class CommentPostRequestBuilder(
    private val author: UserRegisterRequest,
    private val isAnswer: Boolean = false,
) {
    private var content: () -> String = { "Default content" }
    private var anonymity: Boolean = false
    private var pinned: Boolean = false

    @PublishedApi
    internal var comments: MutableList<CommentPostRequest> = mutableListOf()

    fun content(set: () -> String) {
        content = set
    }

    fun content(@Language("md") content: String) {
        this.content = { content }
    }

    fun anonymity(set: () -> Boolean) {
        anonymity = set()
    }

    fun anonymity(anonymity: Boolean) {
        this.anonymity = anonymity
    }

    fun anonymous() {
        anonymity = true
    }

    fun pin() {
        pinned = true
    }

    @SampleDataDslMarker
    inline fun comment(author: UserRegisterRequest, builds: CommentPostRequestBuilder.() -> Unit) {
        comments.add(CommentPostRequestBuilder(author).apply(builds).build())
    }

    fun build(): CommentPostRequest {
        return CommentPostRequest(author, content, anonymity, pinned, comments, isAnswer)
    }
}