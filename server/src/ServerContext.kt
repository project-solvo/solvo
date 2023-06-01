package org.solvo.server

import io.ktor.http.*
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
import org.solvo.server.utils.sampleData.SampleDataBuilder
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
        private val _sharedContents: SharedContentDBControl = SharedContentDBControlImpl()
        private val _comments: CommentDBControl = CommentDBControlImpl(_accounts)
        private val _questions: QuestionDBControl = QuestionDBControlImpl(_comments, _accounts, _sharedContents)
        private val _articles: ArticleDBControl = ArticleDBControlImpl(_courses, _terms, _accounts)

        private val config: ConfigFacade = ConfigFacadeImpl()

        val accounts: AccountDBFacade = AccountDBFacadeImpl(_accounts, _resources)
        val contents: ContentDBFacade = ContentDBFacadeImpl(_courses, _articles, _questions, _comments, _sharedContents)
        val resources: ResourceDBFacade = ResourceDBFacadeImpl(_accounts, _resources)

        fun init() {
            runBlocking {
                if (config.containsConfig("initialized")) return@runBlocking
                incorporateSampleData {
                    sampleData1()
                }
                config.setConfig("initialized")
            }
        }
    }

    object DatabaseFactory {
        fun init() {
            val driverClassName = "org.h2.Driver"
            val dbPath = paths.databasePath()
            val jdbcURL = "jdbc:h2:$dbPath/db;MODE=MYSQL"
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

private fun SampleDataBuilder.sampleData1() {
    val alex = user("Alex", AuthDigest("alex123"))
    val bob = user("Bob", AuthDigest("bob456"))

    val sharedContent1 = sharedContent("Question 1! What is 1 + 1?")
    val image1 = image("./test-resources/image1.jpg", alex, ContentType.Image.JPEG)

    val questionsList = listOf("1a", "1b", "1c", "1d", "2a", "2b", "2c")

    course("50001", "Algorithm Design and Analysis") {
        article("Paper_2022", alex) {
            content("My content")
            anonymous()
            displayName("Paper 2022")
            termYear("2022")
            question("1a") {
                content("![](https://him188.github.io/static/images/Algorithm-2022-1a.png)")
                anonymous()
                sharedContent(sharedContent1)
                answer(alex) {
                    content("I am answering my own question.")
                    anonymous()
                    pin()
                }
                answer(bob) {
                    content("I am answering a question!")
                    comment(alex) {
                        content("Hello bob")
                        comment(bob) {
                            content("Hello alex")
                        }
                        comment(alex) {
                            content("Blah blah")
                        }
                    }
                }
                comment(bob) {
                    content("I am commenting a question.")
                }
            }
            question("1b") {
                content("Haha.")
                sharedContent(sharedContent1)
                anonymous()
            }
            question("2a") {
                content { """
                    Haha.
                    This is some image:
                    ![some image](${image1.url})          
                """.trimIndent() }
                anonymous()
            }
            question("2b") {
                content ( "Haha..!" )
                anonymous()
            }
            comment(bob) {
                content("I am commenting an article!")
                anonymous()
            }
        }
        article("Paper_2021", alex) {
            content("My content")
            anonymous()
            displayName("Paper 2021")
            termYear("2021")
            questions(questionsList)
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