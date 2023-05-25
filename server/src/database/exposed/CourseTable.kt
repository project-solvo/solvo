package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.IntIdTable
import org.solvo.model.utils.DatabaseModel

object CourseTable: IntIdTable("Courses", "courseId") {
    val courseName = varchar("courseName", DatabaseModel.COURSE_NAME_MAX_LENGTH).uniqueIndex()
}
