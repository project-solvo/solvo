package org.solvo.server.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.solvo.server.ServerContext.DatabaseFactory.dbQuery
import org.solvo.server.database.exposed.ConfigTable

interface ConfigFacade {
    suspend fun setConfig(config: String)
    suspend fun containsConfig(config: String): Boolean
}

class ConfigFacadeImpl : ConfigFacade {
    override suspend fun setConfig(config: String): Unit = dbQuery {
        ConfigTable.insert { it[ConfigTable.config] = config }
    }

    override suspend fun containsConfig(config: String): Boolean = dbQuery {
        !ConfigTable.select(ConfigTable.config eq config).empty()
    }
}