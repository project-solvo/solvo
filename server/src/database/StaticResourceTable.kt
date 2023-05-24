package org.solvo.server.database

import org.jetbrains.exposed.dao.id.UUIDTable

object StaticResourceTable : UUIDTable("StaticResources", "ResourceId") {
    val purpose = enumeration<StaticResourcePurpose>("purpose")
    val coid = reference("parentCOID", CommentedObjectTable).nullable()
}

enum class StaticResourcePurpose {
    SERVER_RESOURCE,
    USER_AVATAR,
    TEXT_IMAGE,
}
