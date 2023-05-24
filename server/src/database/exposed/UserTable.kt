package org.solvo.server.database.exposed

import org.jetbrains.exposed.dao.id.UUIDTable
import org.solvo.model.utils.DatabaseModel
import org.solvo.model.utils.UserPermission

object UserTable: UUIDTable("UserInfo", "userId") {
    val username = varchar("username", DatabaseModel.USERNAME_MAX_LENGTH).uniqueIndex()

    val avatar = reference("avatar", StaticResourceTable).nullable()
    val permission = enumeration<UserPermission>("permissionLevel").default(UserPermission.DEFAULT)

    val bannedUntil = long("bannedUntil").nullable()
}