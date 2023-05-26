package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.TermTable

interface TermDBFacade {
    suspend fun getId(term: String): Int?
    suspend fun getOrInsertId(term: String): Int
}

class TermDBFacadeImpl : TermDBFacade {
    override suspend fun getId(term: String): Int? = dbQuery {
        TermTable
            .select(TermTable.termTime eq term)
            .map { it[TermTable.id].value }
            .singleOrNull()
    }

    override suspend fun getOrInsertId(term: String): Int = dbQuery {
        getId(term) ?: TermTable.insertAndGetId {
            it[TermTable.termTime] = term
        }.value
    }
}