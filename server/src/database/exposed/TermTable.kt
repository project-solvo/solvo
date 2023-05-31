package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.IntIdTable
import org.solvo.model.utils.ModelConstraints

object TermTable: IntIdTable("Terms", "termId") {
    val termTime = varchar("termTime", ModelConstraints.TERM_TIME_MAX_LENGTH).uniqueIndex()
}