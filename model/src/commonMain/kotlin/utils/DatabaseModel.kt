package org.solvo.model.utils

class DatabaseModel {
    companion object {
        const val USERNAME_MAX_LENGTH = 16
        const val HASH_SIZE = 32

        const val COURSE_CODE_MAX_LENGTH = 16
        const val COURSE_NAME_MAX_LENGTH = 64
        const val TERM_TIME_MAX_LENGTH = 32

        const val ARTICLE_NAME_MAX_LENGTH = 128
        const val QUESTION_INDEX_MAX_LENGTH = 8

        const val LIGHT_SUB_COMMENTS_AMOUNT = 3
    }
}

enum class UserPermission: Comparable<UserPermission> {
    DEFAULT,
    OPERATOR,
    ROOT,
}