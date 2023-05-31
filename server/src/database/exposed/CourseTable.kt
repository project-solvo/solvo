package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.IntIdTable
import org.solvo.model.utils.ModelConstraints

object CourseTable: IntIdTable("Courses", "courseId") {
    val code = varchar("code", ModelConstraints.COURSE_CODE_MAX_LENGTH).uniqueIndex()
    val name = varchar("name", ModelConstraints.COURSE_NAME_MAX_LENGTH)
}
