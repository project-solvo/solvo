package org.solvo.server.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.solvo.server.database.exposed.*

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:./db"
        Database.connect(jdbcURL, driverClassName)
        transaction {
            SchemaUtils.create(
                AuthTable,
                UserTable,
                StaticResourceTable,
                CourseTable,
                TermTable,
                ArticleTable,
                QuestionTable,
                AnswerTable,
                CommentTable,
                CommentedObjectTable,
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}