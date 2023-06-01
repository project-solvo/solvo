package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.UUIDTable
import org.solvo.model.utils.ModelConstraints
import org.solvo.server.utils.StaticResourcePurpose

object StaticResourceTable : UUIDTable("StaticResources", "ResourceId") {
    val purpose = enumeration<StaticResourcePurpose>("purpose")
    val coid = reference("parentCOID", CommentedObjectTable).nullable()
    val contentType = varchar("contentType", ModelConstraints.CONTENT_TYPE_MAXIMUM_LENGTH)
}
