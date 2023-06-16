@file:Suppress("MemberVisibilityCanBePrivate")

package org.solvo.model.api

import org.solvo.model.annotations.Immutable

// See docs/WebPages.md
@Immutable
object WebPagePathPatterns {
    const val VAR_COURSE_CODE = "{courseCode}"
    const val VAR_ARTICLE_CODE = "{articleCode}"
    const val VAR_QUESTION_CODE = "{questionCo}"

    const val VAR_AUTH_METHOD = "{authMethod}"
    const val VAR_AUTH_METHOD_REGISTER = "register"
    const val VAR_AUTH_METHOD_LOGIN = "login"

    const val VAR_SETTING_GROUP = "{group}"

    private fun relative(path: String): String =
        if (path.startsWith('/') && !path.endsWith('/')) path else "/${path.removeSurrounding("/")}"

    val home = relative("/")
    val auth = relative("/auth/$VAR_AUTH_METHOD")
    val settingsAdmin = relative("/settings/admin/$VAR_SETTING_GROUP")
    val me = relative("/me")
    val courses = relative("/courses/")
    val course = relative("/courses/$VAR_COURSE_CODE")

    val article = "$course/articles/$VAR_ARTICLE_CODE"
    val question = "$article/questions/$VAR_QUESTION_CODE"
}
