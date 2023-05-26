package org.solvo.web.document

import org.solvo.model.api.WebPagePatterns
import org.solvo.model.api.WebPagePatterns.VAR_ARTICLE_CODE
import org.solvo.model.api.WebPagePatterns.VAR_COURSE_CODE
import org.solvo.model.api.WebPagePatterns.VAR_QUESTION_CODE

object WebPagePaths {
    val patterns = WebPagePatterns

    fun home() = patterns.home
    fun auth() = patterns.auth
    fun courses() = patterns.courses
    fun course(code: String) = patterns.course.replace(VAR_COURSE_CODE, code)
    fun article(courseCode: String, articleCode: String) = course(courseCode)
        .replace(VAR_ARTICLE_CODE, articleCode)

    fun question(courseCode: String, articleCode: String, questionCode: String) = article(courseCode, articleCode)
        .replace(VAR_QUESTION_CODE, questionCode)
}