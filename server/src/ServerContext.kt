package org.solvo.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.solvo.server.database.AccountDBFacade
import org.solvo.server.database.AccountDBFacadeImpl
import org.solvo.server.database.exposed.*
import org.solvo.server.utils.*

object ServerContext {
    val localtime: ServerLocalTime = ServerLocalTimeImpl()
    val tokens: TokenGenerator = TokenGeneratorImpl()
    val paths: ServerResourcesPath = ServerResourcesPathImpl()

    val accountDB: AccountDBFacade = AccountDBFacadeImpl()

    fun init() {
        DatabaseFactory.init()
        accountDB.apply {
            runBlocking {
                // initialization here
            }
        }
    }

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
}