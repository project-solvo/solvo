package org.solvo.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.solvo.model.Course
import org.solvo.model.foundation.Uuid
import org.solvo.server.database.AccountDBFacade
import org.solvo.server.database.AccountDBFacadeImpl
import org.solvo.server.database.ContentDBFacade
import org.solvo.server.database.ContentDBFacadeImpl
import org.solvo.server.database.control.*
import org.solvo.server.database.exposed.*
import org.solvo.server.modules.AuthDigest
import org.solvo.server.utils.*

object ServerContext {
    val localtime: ServerLocalTime = ServerLocalTimeImpl()
    val tokens: TokenGenerator = TokenGeneratorImpl()
    val paths: ServerResourcesPath = ServerResourcesPathImpl()
    val files: FileManager = FileManagerImpl()

    fun init() {
        DatabaseFactory.init()
        Databases.init()
    }

    object Databases {
        private val _accounts: AccountDBControl = AccountDBControlImpl()
        private val _resources: ResourcesDBControl = ResourcesDBControlImpl()

        private val _courses: CourseDBControl = CourseDBControlImpl()
        private val _terms: TermDBControl = TermDBControlImpl()
        private val _answers: AnswerDBControl = AnswerDBControlImpl(_accounts)
        private val _questions: QuestionDBControl = QuestionDBControlImpl(_answers, _accounts)
        private val _articles: ArticleDBControl = ArticleDBControlImpl(_courses, _terms, _accounts)

        val accounts: AccountDBFacade = AccountDBFacadeImpl(_accounts, _resources)
        val contents: ContentDBFacade = ContentDBFacadeImpl(_courses, _articles, _questions, _answers, _resources)

        fun init() {
            runBlocking {
                val alex: Uuid
                _accounts.apply {
                    alex = addAccount("Alex", AuthDigest("alex123")) ?: getId("Alex")!!
                    // initialization here
                }
                contents.apply {
                    // TODO: 2023/5/26 this is dummy data
                    newCourse(Course("50001", "Algorithm Design and Analysis"))
                    newCourse(Course("50002", "Software Engineering Design"))
                    newCourse(Course("50003", "Models of Computation"))
                    newCourse(Course("50004", "Operating Systems"))
                    newCourse(Course("50005", "Networks and Communications"))
                    newCourse(Course("50006", "Compilers"))
                    newCourse(Course("50008", "Probability and Statistics"))
                    newCourse(Course("50009", "Symbolic Reasoning"))
                }
//            articles.apply {
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