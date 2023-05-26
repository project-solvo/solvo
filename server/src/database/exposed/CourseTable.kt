package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.IntIdTable
import org.solvo.model.utils.DatabaseModel

object CourseTable: IntIdTable("Courses", "courseId") {
    val code = varchar("code", DatabaseModel.COURSE_CODE_MAX_LENGTH).uniqueIndex()
    val name = varchar("name", DatabaseModel.COURSE_NAME_MAX_LENGTH)
}
