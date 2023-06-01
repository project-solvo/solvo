package org.solvo.server.utils.sampleData

import org.solvo.model.QuestionUpstream
import org.solvo.server.ServerContext
import java.util.*

class QuestionPostRequest(
    val code: String,
    val content: String,
    val anonymity: Boolean,
    val comments: List<CommentPostRequest> = listOf(),
    val sharedContent: SharedContentPostRequest? = null,
) {
    suspend fun submit(
        db: ServerContext.Databases,
        userIdMap: Map<UserRegisterRequest, UUID>,
        sharedContentIdMap: Map<SharedContentPostRequest, UUID>,
        articleId: UUID,
        author: UserRegisterRequest,
    ) {
        val sharedContentId = sharedContent?.let { sharedContentIdMap[it] }

        db.contents.apply {
            val questionId = postQuestion(
                question = QuestionUpstream(content, anonymity, sharedContentId),
                authorId = userIdMap[author]!!,
                articleId = articleId,
                code = code,
            )!!
            comments.map { commentRequest -> commentRequest.submit(db, userIdMap, questionId) }
        }
    }
}

@SampleDataDslMarker
class QuestionPostRequestBuilder(
    private val code: String
) {
    private var content = ""
    private var anonymity = false
    private var sharedContent: SharedContentPostRequest? = null

    @PublishedApi
    internal var comments: MutableList<CommentPostRequest> = mutableListOf()

    fun content(set: () -> String) {
        content = set()
    }

    fun content(content: String) {
        this.content = content
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
        comments.add(CommentPostRequestBuilder(author, isAnswer = false).apply(builds).build())
    }

    @SampleDataDslMarker
    inline fun answer(author: UserRegisterRequest, builds: CommentPostRequestBuilder.() -> Unit) {
        comments.add(CommentPostRequestBuilder(author, isAnswer = true).apply(builds).build())
    }

    fun build(): QuestionPostRequest {
        return QuestionPostRequest(code, content, anonymity, comments, sharedContent)
    }
}