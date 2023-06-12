package org.solvo.server.database.exposed

import org.solvo.model.api.communication.CommentKind

object CommentTable: COIDTable("Comments") {
    val parent = reference("parentCOID", CommentedObjectTable)

    val kind = enumeration<CommentKind>("kind")
    val pinned = bool("pinned").default(false)
}