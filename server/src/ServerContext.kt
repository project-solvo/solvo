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
import org.solvo.server.utils.*
import org.solvo.server.utils.events.EventSessionHandler
import org.solvo.server.utils.events.EventSessionHandlerImpl
import org.solvo.server.utils.sampleData.builder.incorporateSampleData
import org.solvo.server.utils.sampleData.data.sampleData1
import org.solvo.server.utils.sampleData.data.sampleData2

object ServerContext {
    val localtime: ServerLocalTime = ServerLocalTimeImpl()
    val tokens: TokenGenerator = TokenGeneratorDBImpl(Databases.accounts)
    val paths: ServerResourcesPath = ServerResourcesPathImpl()
    val files: FileManager = FileManagerImpl()
    val events: EventSessionHandler = EventSessionHandlerImpl()

    fun init() {
        DatabaseFactory.init()
        Databases.init()
    }

    object Databases {
        private val _accounts: AccountDBControl = AccountDBControlImpl()
        private val _tokens: AuthTokenDBControl = AuthTokenDBControlImpl()
        private val _resources: ResourcesDBControl = ResourcesDBControlImpl()

        private val _courses: CourseDBControl = CourseDBControlImpl()
        private val _terms: TermDBControl = TermDBControlImpl()
        private val _texts: TextDBControl = TextDBControlImpl()
        private val _reactions: ReactionDBControl = ReactionDBControlImpl()
        private val _comments: CommentDBControl = CommentDBControlImpl(_accounts, _texts)
        private val _questions: QuestionDBControl = QuestionDBControlImpl(_accounts, _texts)
        private val _articles: ArticleDBControl = ArticleDBControlImpl(_courses, _terms, _accounts, _texts)

        private val config: ConfigFacade = ConfigFacadeImpl()

        val accounts: AccountDBFacade = AccountDBFacadeImpl(_accounts, _tokens, _resources)
        val contents: ContentDBFacade =
            ContentDBFacadeImpl(_courses, _articles, _questions, _comments, _texts, _reactions)
        val resources: ResourceDBFacade = ResourceDBFacadeImpl(_accounts, _resources)

        fun init() {
            runBlocking {
                if (config.containsConfig("initialized")) return@runBlocking
                incorporateSampleData {
                    sampleData1()
                    sampleData2()
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
                    AuthTokenTable,
                    UserTable,
                    StaticResourceTable,
                    CourseTable,
                    TermTable,
                    ArticleTable,
                    QuestionTable,
                    CommentTable,
                    CommentedObjectTable,
                    ReactionTable,
                    AnswerCodeTable
                )
            }
        }

        suspend fun <T> dbQuery(block: suspend () -> T): T =
            newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}
