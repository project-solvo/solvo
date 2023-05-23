package org.solvo.server.database

import org.jetbrains.exposed.dao.id.UUIDTable
import org.solvo.model.utils.DatabaseModel

object UserTable: UUIDTable("UserInfo", "userId") {
    val username = varchar("username", DatabaseModel.USERNAME_MAX_LENGTH).uniqueIndex()

    val avatar = reference("avatar", StaticResourceTable).nullable()
    val permission = integer("permissionLevel").default(UserPermission.DEFAULT)
}

class UserPermission {
    companion object {
        const val DEFAULT = 0
        const val ROOT = 1
    }
}