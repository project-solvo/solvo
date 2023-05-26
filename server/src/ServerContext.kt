package org.solvo.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.solvo.model.Course
import org.solvo.model.foundation.Uuid
import org.solvo.server.database.*
import org.solvo.server.database.exposed.*
import org.solvo.server.modules.AuthDigest
import org.solvo.server.utils.*

object ServerContext {
    val localtime: ServerLocalTime = ServerLocalTimeImpl()
    val tokens: TokenGenerator = TokenGeneratorImpl()
    val paths: ServerResourcesPath = ServerResourcesPathImpl()
    val files: FileManager = FileManagerImpl()

    object Databases {
        val accounts: AccountDBFacade = AccountDBFacadeImpl()
        val resources: ResourcesDBFacade = ResourcesDBFacadeImpl()

        val courses: CourseDBFacade = CourseDBFacadeImpl()
        val terms: TermDBFacade = TermDBFacadeImpl()
        val answers: AnswerDBFacade = AnswerDBFacadeImpl(accounts)
        val questions: QuestionDBFacade = QuestionDBFacadeImpl(answers, accounts)
        val articles: ArticleDBFacade = ArticleDBFacadeImpl(courses, terms, accounts)
    }

    fun init() {
        DatabaseFactory.init()
        runBlocking {
            val alex: Uuid
            Databases.accounts.apply {
                alex = addAccount("Alex", AuthDigest("alex123")) ?: getId("Alex")!!
                // initialization here
            }
            Databases.resources.apply {
                // initialization here
            }
            Databases.courses.apply {
                // TODO: 2023/5/26 this is dummy data
                getOrInsertId(Course("50001", "Algorithm Design and Analysis"))
                getOrInsertId(Course("50002", "Software Engineering Design"))
                getOrInsertId(Course("50003", "Models of Computation"))
                getOrInsertId(Course("50004", "Operating Systems"))
                getOrInsertId(Course("50005", "Networks and Communications"))
                getOrInsertId(Course("50006", "Compilers"))
                getOrInsertId(Course("50008", "Probability and Statistics"))
                getOrInsertId(Course("50009", "Symbolic Reasoning"))
            }
//            Databases.articles.apply {
//                post(
//                    ArticleUpstream(
//                        content = "My content",
//                        anonymity = true,
//                        name = "Paper 2022",
//                        courseCode = "50001",
//                        termYear = "2022",
//                        questions = listOf()
//                    ),
//                    User(
//                        alex,
//                        "",
//                        null
//                    )
//                )
//            }
        }
    }

    object DatabaseFactory {
        fun init() {
            val driverClassName = "org.h2.Driver"
            val jdbcURL = "jdbc:h2:./db;MODE=MYSQL"
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