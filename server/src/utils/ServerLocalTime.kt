package org.solvo.server.utils

interface ServerLocalTime {
    fun now(): Long
}
class ServerLocalTimeImpl: ServerLocalTime {
    override fun now(): Long {
        return System.currentTimeMillis()
    }
}
