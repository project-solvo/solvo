package org.solvo.server.utils.sampleData.builder

import org.intellij.lang.annotations.Language
import org.solvo.model.api.communication.CommentKind
import org.solvo.model.api.communication.QuestionEditRequest
import org.solvo.model.utils.NonBlankString
import org.solvo.server.ServerContext
import org.solvo.server.utils.sampleData.SampleDataDslMarker
import java.util.*

class QuestionPostRequest(
    val code: String,
    val content: () -> String,
    val anonymity: Boolean,
    val comments: List<CommentPostRequest> = listOf(),
    val sharedContent: SharedContentPostRequest? = null,
) {
    suspend fun submit(
        db: ServerContext.Databases,
        articleId: UUID,
        author: UserRegisterRequest,
    ) {
        val sharedContentId = sharedContent?.id

        db.contents.apply {
            val questionId = createQuestion(
                questionCode = NonBlankString.fromString(code),
                articleId = articleId,
                authorId = author.uid
            )!!

            editQuestion(
                request = QuestionEditRequest(
                    NonBlankString.fromString(content()), anonymity, sharedContentId
                ),
                userId = author.uid,
                questionId = questionId
            )
            comments.map { commentRequest -> commentRequest.submit(db, questionId) }
        }
    }
}

@SampleDataDslMarker
class QuestionPostRequestBuilder(
    private val code: String
) {
    private var content: () -> String = { "Default content" }
    private var anonymity = false
    private var sharedContent: SharedContentPostRequest? = null

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

    fun sharedContent(set: () -> SharedContentPostRequest) {
        sharedContent = set()
    }

    fun sharedContent(sharedContent: SharedContentPostRequest) {
        this.sharedContent = sharedContent
    }

    @SampleDataDslMarker
    inline fun comment(author: UserRegisterRequest, builds: CommentPostRequestBuilder.() -> Unit) {
        comments.add(CommentPostRequestBuilder(author, CommentKind.COMMENT).apply(builds).build())
    }

    @SampleDataDslMarker
    inline fun answer(author: UserRegisterRequest, builds: CommentPostRequestBuilder.() -> Unit) {
        comments.add(CommentPostRequestBuilder(author, CommentKind.ANSWER).apply(builds).build())
    }

    @SampleDataDslMarker
    inline fun thought(author: UserRegisterRequest, builds: CommentPostRequestBuilder.() -> Unit) {
        comments.add(CommentPostRequestBuilder(author, CommentKind.THOUGHT).apply(builds).build())
    }

    fun build(): QuestionPostRequest {
        return QuestionPostRequest(code, content, anonymity, comments, sharedContent)
    }
}