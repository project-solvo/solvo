package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.solvo.model.Course
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.CourseTable

interface CourseDBFacade {
    suspend fun all(): List<Course>
    suspend fun getId(courseCode: String): Int?
    suspend fun getCourse(courseId: Int): Course?
    suspend fun getOrInsertId(course: Course): Int
}

class CourseDBFacadeImpl : CourseDBFacade {
    override suspend fun all(): List<Course> = dbQuery {
        CourseTable.selectAll().map { Course(it[CourseTable.code], it[CourseTable.name]) }
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
            .map { Course(it[CourseTable.code], it[CourseTable.name]) }
            .singleOrNull()
    }

    override suspend fun getOrInsertId(course: Course): Int = dbQuery {
        getId(course.code) ?: CourseTable.insertAndGetId {
            it[CourseTable.code] = course.code
            it[CourseTable.name] = course.name
        }.value
    }

}