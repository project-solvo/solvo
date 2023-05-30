package org.solvo.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.solvo.model.ArticleUpstream
import org.solvo.model.Course
import org.solvo.model.QuestionUpstream
import org.solvo.model.foundation.Uuid
import org.solvo.server.database.*
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
        private val _comments: CommentDBControl = CommentDBControlImpl(_accounts)
        private val _questions: QuestionDBControl = QuestionDBControlImpl(_comments, _accounts)
        private val _articles: ArticleDBControl = ArticleDBControlImpl(_courses, _terms, _accounts)

        private val config: ConfigFacade = ConfigFacadeImpl()

        val accounts: AccountDBFacade = AccountDBFacadeImpl(_accounts, _resources)
        val contents: ContentDBFacade = ContentDBFacadeImpl(_courses, _articles, _questions, _comments, _resources)

        fun init() {
            runBlocking {
                if (config.containsConfig("initialized")) return@runBlocking
                val alex: Uuid
                accounts.apply {
                    register("Alex", AuthDigest("alex123"))
                    val token = login("Alex", AuthDigest("alex123")).token
                    alex = tokens.matchToken(token)!!
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


                    val questionList = mutableListOf<QuestionUpstream>()

                    for (i in 1 until 3) {
                        for (j in 'a' until 'f') {
                            questionList.add(QuestionUpstream("Haha", true, "$i$j"))
                        }
                    }
                    postArticle(
                        article = ArticleUpstream(
                            content = "My content",
                            anonymity = true,
                            name = "Paper 2022",
                            termYear = "2022",
                            questions = questionList,
                        ),
                        authorId = alex,
                        courseCode = "50001"
                    )
                }
                config.setConfig("initialized")
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
                    ConfigTable,
                    AuthTable,
                    UserTable,
                    StaticResourceTable,
                    CourseTable,
                    TermTable,
                    ArticleTable,
                    QuestionTable,
                    CommentTable,
                    CommentedObjectTable,
                )
            }
        }

        suspend fun <T> dbQuery(block: suspend () -> T): T =
            newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}