package org.solvo.server.utils.sampleData

import org.solvo.model.ArticleUpstream
import org.solvo.model.Course
import org.solvo.server.ServerContext
import java.util.*

@SampleDataDslMarker
class UserRegisterRequest(
    val username: String,
    val password: ByteArray,
)

@SampleDataDslMarker
class CoursePostRequest(
    val code: String,
    val name: String,
    val articles: List<ArticlePostRequest>,
)

@SampleDataDslMarker
class ArticlePostRequest(
    val article: ArticleUpstream,
    val author: UserRegisterRequest,
)

@SampleDataDslMarker
class SampleDataBuilder {
    private val users: MutableList<UserRegisterRequest> = mutableListOf()
    private val courses: MutableList<CoursePostRequest> = mutableListOf()

    @SampleDataDslMarker
    fun user(username: String, password: ByteArray): UserRegisterRequest {
        return UserRegisterRequest(username, password).also { users.add(it) }
    }

    @SampleDataDslMarker
    fun course(
        code: String,
        name: String,
        articleBuilds: MutableList<ArticlePostRequest>.() -> Unit = {},
    ): CoursePostRequest {
        val articles = mutableListOf<ArticlePostRequest>().apply(articleBuilds)
        return CoursePostRequest(code, name, articles).also { courses.add(it) }
    }

    @SampleDataDslMarker
    fun MutableList<ArticlePostRequest>.article(builds: ArticlePostRequestBuilder.() -> Unit) {
        add(ArticlePostRequestBuilder().apply(builds).build())
    }

    suspend fun build(db: ServerContext.Databases) {
        val userIdMap: MutableMap<UserRegisterRequest, UUID> = mutableMapOf()
        users.map { userRequest ->
            db.accounts.apply {
                register(userRequest.username, userRequest.password)
                val token = login(userRequest.username, userRequest.password).token
                userIdMap[userRequest] = ServerContext.tokens.matchToken(token)!!
            }
        }
        courses.map { course ->
            db.contents.apply {
                newCourse(Course(course.code, course.name))
                course.articles.map { articleRequest ->
                    postArticle(
                        article = articleRequest.article,
                        authorId = userIdMap[articleRequest.author]!!,
                        courseCode = course.code,
                    )
                }
            }
        }
    }
}

suspend inline fun ServerContext.Databases.incorporateSampleData(builds: SampleDataBuilder.() -> Unit) {
    SampleDataBuilder().apply { builds() }.build(this)
}
