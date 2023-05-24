package org.solvo.server.utils

enum class StaticResourcePurpose {
    SERVER_RESOURCE,
    USER_AVATAR,
    TEXT_IMAGE;

    override fun toString(): String {
        return when (this) {
            SERVER_RESOURCE -> "server"
            USER_AVATAR -> "avatars"
            TEXT_IMAGE -> "images"
        }
    }
}