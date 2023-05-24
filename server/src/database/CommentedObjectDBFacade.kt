package org.solvo.server.database

import java.util.*

interface CommentedObjectDBFacade<T> {
    suspend fun post(t: T): UUID?
    suspend fun modify(t: T): Boolean
    suspend fun delete(coid: UUID): Boolean
    suspend fun view(coid: UUID): T?
    suspend fun like(uid: UUID, coid: UUID): Boolean
    suspend fun unLike(uid: UUID, coid: UUID): Boolean
}