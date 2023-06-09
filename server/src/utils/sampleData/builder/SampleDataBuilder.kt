package org.solvo.server.utils.sampleData.builder

import io.ktor.http.*
import org.solvo.server.ServerContext
import org.solvo.server.utils.StaticResourcePurpose
import org.solvo.server.utils.sampleData.SampleDataDslMarker

@SampleDataDslMarker
class SampleDataBuilder {
    private val users: MutableList<UserRegisterRequest> = mutableListOf()
    private val sharedContents: MutableList<SharedContentPostRequest> = mutableListOf()
    private val images: MutableList<ImagePostRequest> = mutableListOf()

    @PublishedApi
    internal val courses: MutableList<CoursePostRequest> = mutableListOf()

    @SampleDataDslMarker
    fun user(
        username: String,
        password: ByteArray,
        builds: UserDataBuilder.() -> Unit = {},
    ): UserRegisterRequest {
        return UserDataBuilder(username, password).apply(builds).build().also { users.add(it) }
    }

    @SampleDataDslMarker
    fun sharedContent(content: String): SharedContentPostRequest {
        return SharedContentPostRequest { content }.also { sharedContents.add(it) }
    }

    @SampleDataDslMarker
    fun sharedContent(set: () -> String): SharedContentPostRequest {
        return SharedContentPostRequest(set).also { sharedContents.add(it) }
    }

    @SampleDataDslMarker
    fun image(path: String, user: UserRegisterRequest, contentType: ContentType = ContentType.Any): ImagePostRequest {
        return ImagePostRequest(path, contentType, user, StaticResourcePurpose.TEXT_IMAGE).also { images.add(it) }
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
        users.forEach { it.submit(db) }
        images.forEach { it.submit(db) }
        sharedContents.forEach { it.submit(db) }
        courses.forEach { it.submit(db) }
    }
}

suspend inline fun ServerContext.Databases.incorporateSampleData(builds: SampleDataBuilder.() -> Unit) {
    SampleDataBuilder().apply { builds() }.submit(this)
}
