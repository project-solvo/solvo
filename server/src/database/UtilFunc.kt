package org.solvo.server.database

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager


fun <T : Table> T.updateIfNotEmpty(
    where: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
    limit: Int? = null,
    body: T.(UpdateStatement) -> Unit
): Int {
    val query = UpdateStatement(this, limit, where?.let { SqlExpressionBuilder.it() })
    body(query)
    if (query.firstDataSet.isEmpty()) return 0
    return query.execute(TransactionManager.current())!!
}
