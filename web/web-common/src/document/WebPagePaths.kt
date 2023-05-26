package org.solvo.web.document

import org.solvo.model.api.WebPagePathPatterns
import org.solvo.model.api.WebPagePathPatterns.VAR_ARTICLE_CODE
import org.solvo.model.api.WebPagePathPatterns.VAR_COURSE_CODE
import org.solvo.model.api.WebPagePathPatterns.VAR_QUESTION_CODE

object WebPagePaths {
    val patterns = WebPagePathPatterns

    fun home() = patterns.home
    fun auth() = patterns.auth
    fun courses() = patterns.courses
    fun course(code: String) = patterns.course.replace(VAR_COURSE_CODE, code)
    fun article(courseCode: String, articleCode: String) = course(courseCode)
        .replace(VAR_ARTICLE_CODE, articleCode)

    fun question(courseCode: String, articleCode: String, questionCode: String) = article(courseCode, articleCode)
        .replace(VAR_QUESTION_CODE, questionCode)
}