package org.solvo.web.document.parameters

internal object PathParameterParser {
    fun parse(pattern: String, pathname: String): Map<String, String> {
        // `/courses/`
        val expectList = pattern.split("/").dropWhile { it.isBlank() }
        val actualList = pathname.split("/").dropWhile { it.isBlank() }

        val result = mutableMapOf<String, String>()
        expectList.zip(actualList)
            .forEach { (expect, actual) ->
                if (expect.startsWith("{")) {
                    // is a param
                    result[expect] = actual
                } else {
                    if (expect != actual) {
                        throw IllegalStateException("URL segment mismatch: expect='$expect', actual='$actual'. Full url='$pathname'. Pattern='$pattern'")
                    }
                }
            }
        return result
    }
}