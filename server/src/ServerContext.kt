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
import org.solvo.server.utils.eventHandler.CommentEventHandler
import org.solvo.server.utils.eventHandler.CommentEventHandlerImpl
import org.solvo.server.utils.sampleData.builder.incorporateSampleData
import org.solvo.server.utils.sampleData.data.sampleData1

object ServerContext {
    val localtime: ServerLocalTime = ServerLocalTimeImpl()
    val tokens: TokenGenerator = TokenGeneratorDBImpl(Databases.accounts)
    val paths: ServerResourcesPath = ServerResourcesPathImpl()
    val files: FileManager = FileManagerImpl()

    fun init() {
        DatabaseFactory.init()
        Databases.init()
    }

    object Events {
        val commentUpdates: CommentEventHandler = CommentEventHandlerImpl()
    }

    object Databases {
        private val _accounts: AccountDBControl = AccountDBControlImpl()
        private val _tokens: AuthTokenDBControl = AuthTokenDBControlImpl()
        private val _resources: ResourcesDBControl = ResourcesDBControlImpl()

        private val _courses: CourseDBControl = CourseDBControlImpl()
        private val _terms: TermDBControl = TermDBControlImpl()
        private val _sharedContents: SharedContentDBControl = SharedContentDBControlImpl()
        private val _reactions: ReactionDBControl = ReactionDBControlImpl()
        private val _comments: CommentDBControl = CommentDBControlImpl(_accounts)
        private val _questions: QuestionDBControl = QuestionDBControlImpl(_comments, _accounts, _sharedContents)
        private val _articles: ArticleDBControl = ArticleDBControlImpl(_courses, _terms, _accounts)

        private val config: ConfigFacade = ConfigFacadeImpl()

        val accounts: AccountDBFacade = AccountDBFacadeImpl(_accounts, _tokens, _resources)
        val contents: ContentDBFacade =
            ContentDBFacadeImpl(_courses, _articles, _questions, _comments, _sharedContents, _reactions)
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
                )
            }
        }

        suspend fun <T> dbQuery(block: suspend () -> T): T =
            newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}
