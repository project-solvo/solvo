@file:UseSerializers(UuidAsStringSerializer::class)

package org.solvo.model.api.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.solvo.model.api.communication.ArticleDownstream
import org.solvo.model.api.communication.Course
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer

@Serializable
class UpdateCourseEvent(
    val course: Course,
    val articles: List<Uuid>,
) : ClusteredEvent, CoursePageEvent {
    override val dispatchedEvents: List<DispatchedUpdateCourseEvent>
        get() = articles.map { DispatchedUpdateCourseEvent(course, it) }
    override val courseCode: String
        get() = course.code.str
}

@Serializable
class DispatchedUpdateCourseEvent(
    val course: Course,
    override val articleCoid: Uuid,
) : ArticleSettingPageEvent

@Serializable
class RemoveCourseEvent(
    override val courseCode: String,
) : CoursePageEvent

