package org.solvo.model.utils

class ModelConstraints {
    companion object {
        const val USERNAME_MAX_LENGTH = 16
        const val HASH_SIZE = 32

        const val COURSE_CODE_MAX_LENGTH = 16
        const val COURSE_NAME_MAX_LENGTH = 64
        const val TERM_TIME_MAX_LENGTH = 32

        const val ARTICLE_NAME_MAX_LENGTH = 128
        const val ARTICLE_CODE_MAX_LENGTH = 128
        const val QUESTION_CODE_MAX_LENGTH = 8

        const val LIGHT_SUB_COMMENTS_AMOUNT = 3

        const val CONTENT_TYPE_MAXIMUM_LENGTH = 256
    }
}

enum class UserPermission : Comparable<UserPermission> {
    DEFAULT,
    OPERATOR,
    ROOT,
}