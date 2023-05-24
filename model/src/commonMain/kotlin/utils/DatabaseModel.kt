package org.solvo.model.utils

class DatabaseModel {
    companion object {
        const val USERNAME_MAX_LENGTH = 16
        const val HASH_SIZE = 32

        const val COURSE_NAME_MAX_LENGTH = 64
        const val TERM_TIME_MAX_LENGTH = 32
    }
}

enum class UserPermission: Comparable<UserPermission> {
    DEFAULT,
    OPERATOR,
    ROOT,
}