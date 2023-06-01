package org.solvo.server.utils.sampleData

import org.solvo.model.SharedContent
import org.solvo.server.ServerContext
import java.util.*

class UserRegisterRequest(
    val username: String,
    val password: ByteArray,
) {
    suspend fun submit(db: ServerContext.Databases, userIdMap: MutableMap<UserRegisterRequest, UUID>) {
        db.accounts.apply {
            register(username, password)
            val token = login(username, password).token
            userIdMap[this@UserRegisterRequest] = ServerContext.tokens.matchToken(token)!!
        }
    }
}

class SharedContentPostRequest(
    val content: String
) {
    suspend fun submit(
        db: ServerContext.Databases,
        userIdMap: Map<UserRegisterRequest, UUID>,
        sharedContentIdMap: MutableMap<SharedContentPostRequest, UUID>,
    ) {
        db.contents.apply {
            val id = postSharedContent(SharedContent(content))!!
            sharedContentIdMap[this@SharedContentPostRequest] = id
        }
    }
}

@SampleDataDslMarker
class SampleDataBuilder {
    private val users: MutableList<UserRegisterRequest> = mutableListOf()
    private val sharedContents: MutableList<SharedContentPostRequest> = mutableListOf()

    @PublishedApi
    internal val courses: MutableList<CoursePostRequest> = mutableListOf()

    @SampleDataDslMarker
    fun user(username: String, password: ByteArray): UserRegisterRequest {
        return UserRegisterRequest(username, password).also { users.add(it) }
    }

    @SampleDataDslMarker
    fun sharedContent(content: String): SharedContentPostRequest {
        return SharedContentPostRequest(content).also { sharedContents.add(it) }
    }

    @SampleDataDslMarker
    inline fun course(
        code: String,
        name: String,
        builds: CoursePostRequestBuilder.() -> Unit = {},
    ): CoursePostRequest {
        return CoursePostRequestBuilder(code, name).apply(builds).build().also { courses.add(it) }
    }

    suspend fun submit(db: ServerContext.Databases) {
        val userIdMap: MutableMap<UserRegisterRequest, UUID> = mutableMapOf()
        users.forEach { userRequest -> userRequest.submit(db, userIdMap) }

        val sharedContentIdMap: MutableMap<SharedContentPostRequest, UUID> = mutableMapOf()
        sharedContents.forEach { it.submit(db, userIdMap, sharedContentIdMap) }

        courses.forEach { courseRequest -> courseRequest.submit(db, userIdMap, sharedContentIdMap) }
    }
}

suspend inline fun ServerContext.Databases.incorporateSampleData(builds: SampleDataBuilder.() -> Unit) {
    SampleDataBuilder().apply { builds() }.submit(this)
}
