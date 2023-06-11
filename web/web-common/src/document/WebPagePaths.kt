package org.solvo.web.document

import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.WebPagePathPatterns.VAR_ARTICLE_CODE
import org.solvo.model.api.WebPagePathPatterns.VAR_COURSE_CODE
import org.solvo.model.api.WebPagePathPatterns.VAR_QUESTION_CODE
import org.solvo.web.session.item

abstract class WebPagePaths {
    val patterns get() = WebPagePathPatterns

    fun home() = patterns.home
    fun authReturnOrHome(): String {
        return (LocalAuthReturnPath.value ?: patterns.home).also {
            LocalAuthReturnPath.value = null
        }
    }

    fun auth(): String {
        LocalAuthReturnPath.value = window.location.href
        return patterns.auth
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

internal val LocalAuthReturnPath = localStorage.item("authReturnPath")