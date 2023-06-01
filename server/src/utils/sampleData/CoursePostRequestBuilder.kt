package org.solvo.server.utils.sampleData

import org.solvo.model.Course
import org.solvo.server.ServerContext
import java.util.*


class CoursePostRequest(
    val code: String,
    val name: String,
    val articles: List<ArticlePostRequest>,
) {
    suspend fun submit(
        db: ServerContext.Databases,
        userIdMap: Map<UserRegisterRequest, UUID>,
        sharedContentIdMap: Map<SharedContentPostRequest, UUID>,
    ) {
        db.contents.apply {
            newCourse(Course(code, name))
            articles.map { articleRequest -> articleRequest.submit(db, userIdMap, sharedContentIdMap, code) }
        }
    }
}

@SampleDataDslMarker
class CoursePostRequestBuilder(
    private var code: String,
    private var name: String
) {
    @PublishedApi
    internal val articles: MutableList<ArticlePostRequest> = mutableListOf()

    @SampleDataDslMarker
    inline fun article(
        code: String,
        author: UserRegisterRequest,
        builds: ArticlePostRequestBuilder.() -> Unit
    ): ArticlePostRequest {
        return ArticlePostRequestBuilder(code, author).apply(builds).build().also { articles.add(it) }
    }

    fun build(): CoursePostRequest {
        return CoursePostRequest(code, name, articles)
    }
}