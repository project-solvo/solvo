package org.solvo.server.database.control

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.TermTable

interface TermDBControl {
    suspend fun getId(term: String): Int?
    suspend fun getTerm(termId: Int): String?
    suspend fun getOrInsertId(term: String): Int
}

class TermDBControlImpl : TermDBControl {
    override suspend fun getId(term: String): Int? = dbQuery {
        TermTable
            .select(TermTable.termTime eq term)
            .map { it[TermTable.id].value }
            .singleOrNull()
    }

    override suspend fun getTerm(termId: Int): String? = dbQuery {
        TermTable
            .select(TermTable.id eq termId)
            .map { it[TermTable.termTime] }
            .singleOrNull()
    }

    override suspend fun getOrInsertId(term: String): Int = dbQuery {
        getId(term) ?: TermTable.insertAndGetId {
            it[TermTable.termTime] = term
        }.value
    }
}