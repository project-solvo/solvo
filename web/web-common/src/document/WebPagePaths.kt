package org.solvo.web.document

import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.WebPagePathPatterns.VAR_ARTICLE_CODE
import org.solvo.model.api.WebPagePathPatterns.VAR_AUTH_METHOD
import org.solvo.model.api.WebPagePathPatterns.VAR_AUTH_METHOD_LOGIN
import org.solvo.model.api.WebPagePathPatterns.VAR_AUTH_METHOD_REGISTER
import org.solvo.model.api.WebPagePathPatterns.VAR_COURSE_CODE
import org.solvo.model.api.WebPagePathPatterns.VAR_QUESTION_CODE
import org.solvo.model.api.WebPagePathPatterns.VAR_SETTING_GROUP
import org.solvo.web.session.item

abstract class WebPagePaths {
    val patterns get() = WebPagePathPatterns

    fun home() = patterns.home
    fun authReturnOrHome(): String {
        return (LocalRefer.value ?: patterns.home).also {
            LocalRefer.value = null
        }
    }

    fun auth(isRegister: Boolean = false, recordRefer: Boolean = true): String {
        if (recordRefer) {
            LocalRefer.value = window.location.href
        }
        return patterns.auth.replace(
            VAR_AUTH_METHOD,
            if (isRegister) VAR_AUTH_METHOD_REGISTER else VAR_AUTH_METHOD_LOGIN
        )
    }

    fun user(): String {
        return patterns.me
    }

    fun settingsAdmin(group: String?): String {
        return patterns.settingsAdmin.replaceNotNull(VAR_SETTING_GROUP, group)
    }

    fun courses() = patterns.courses
    fun course(code: String) = patterns.course
        .replace(VAR_COURSE_CODE, code)

    fun article(courseCode: String, articleCode: String) = patterns.article
        .replace(VAR_COURSE_CODE, courseCode)
        .replace(VAR_ARTICLE_CODE, articleCode)

    fun question(courseCode: String, articleCode: String, questionCode: String) = patterns.question
        .replace(VAR_COURSE_CODE, courseCode)
        .replace(VAR_ARTICLE_CODE, articleCode)
        .replace(VAR_QUESTION_CODE, questionCode)


}

internal val LocalRefer = localStorage.item("authReturnPath")

private fun String.replaceNotNull(oldValue: String, newValue: String?, default: String = this): String =
    newValue?.let { replace(oldValue, it) } ?: default
