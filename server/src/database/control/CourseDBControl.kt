package org.solvo.server.database.control

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.solvo.model.api.communication.Course
import org.solvo.model.utils.ModelConstraints
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.CourseTable

interface CourseDBControl {
    suspend fun all(): List<Course>
    suspend fun getId(courseCode: String): Int?
    suspend fun getCourse(courseId: Int): Course?
    suspend fun getIdOrInsert(course: Course): Int
    suspend fun insert(course: Course): Int?
    suspend fun edit(courseId: Int, course: Course): Boolean
}

class CourseDBControlImpl : CourseDBControl {
    override suspend fun all(): List<Course> = dbQuery {
        CourseTable.selectAll().map { Course.fromString(it[CourseTable.code], it[CourseTable.name]) }
    }

    override suspend fun getId(courseCode: String): Int? = dbQuery {
        CourseTable
            .select(CourseTable.code eq courseCode)
            .map { it[CourseTable.id].value }
            .singleOrNull()
    }

    override suspend fun getCourse(courseId: Int): Course? = dbQuery {
        CourseTable
            .select(CourseTable.id eq courseId)
            .map { Course.fromString(it[CourseTable.code], it[CourseTable.name]) }
            .singleOrNull()
    }

    override suspend fun getIdOrInsert(course: Course): Int = dbQuery {
        getId(course.code.str) ?: CourseTable.insertAndGetId {
            it[CourseTable.code] = course.code.str
            it[CourseTable.name] = course.name.str
        }.value
    }

    override suspend fun insert(course: Course): Int? = dbQuery {
        if (course.code.str.length > ModelConstraints.COURSE_CODE_MAX_LENGTH
            || course.name.str.length > ModelConstraints.COURSE_NAME_MAX_LENGTH) {
            return@dbQuery null
        }

        CourseTable.insertIgnoreAndGetId {
            it[CourseTable.code] = course.code.str
            it[CourseTable.name] = course.name.str
        }?.value
    }

    override suspend fun edit(courseId: Int, course: Course): Boolean = dbQuery {
        if (course.code.str.length > ModelConstraints.COURSE_CODE_MAX_LENGTH
            || course.name.str.length > ModelConstraints.COURSE_NAME_MAX_LENGTH) {
            return@dbQuery false
        }

        CourseTable.update({CourseTable.id eq courseId}) {
            it[CourseTable.code] = course.code.str
            it[CourseTable.name] = course.name.str
        } > 0
    }
}