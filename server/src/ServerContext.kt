package org.solvo.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.solvo.server.database.*
import org.solvo.server.database.control.*
import org.solvo.server.database.exposed.*
import org.solvo.server.modules.AuthDigest
import org.solvo.server.utils.*
import org.solvo.server.utils.sampleData.incorporateSampleData

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
                incorporateSampleData {
                    val alex = user("Alex", AuthDigest("alex123"))

                    val questionsList = listOf("1a", "1b", "1c", "1d", "2a", "2b", "2c")

                    course("50001", "Algorithm Design and Analysis") {
                        article("Paper_2022", alex) {
                            content { "My content" }
                            anonymity { true }
                            displayName { "Paper 2022" }
                            termYear { "2022" }
                            questions { questionsList }
                        }
                        article("Paper_2021", alex) {
                            content { "My content" }
                            anonymity { true }
                            displayName { "Paper 2021" }
                            termYear { "2021" }
                            questions { questionsList }
                        }
                    }
                    course("50002", "Software Engineering Design")
                    course("50003", "Models of Computation")
                    course("50004", "Operating Systems")
                    course("50005", "Networks and Communications")
                    course("50006", "Compilers")
                    course("50008", "Probability and Statistics")
                    course("50009", "Symbolic Reasoning")
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