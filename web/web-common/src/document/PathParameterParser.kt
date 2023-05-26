package org.solvo.web.document

internal object PathParameterParser {
    fun parse(pattern: String, url: String): Map<String, String> {
        // `/courses/`
        val expectList = pattern.split("/").dropWhile { it.isBlank() }
        val actualList = url.split("/").dropWhile { it.isBlank() }

        val result = mutableMapOf<String, String>()
        expectList.zip(actualList)
            .forEach { (expect, actual) ->
                if (expect.startsWith("{")) {
                    // is a param
                    result[expect] = actual
                } else {
                    if (expect != actual) {
                        throw IllegalStateException("URL segment mismatch: expect='$expect', actual='$actual'. Full url='$url'. Pattern='$pattern'")
                    }
                }
            }
        return result
    }
}