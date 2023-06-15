package org.solvo.server.utils.sampleData.data

import io.ktor.http.*
import org.solvo.server.modules.AuthDigest
import org.solvo.server.utils.sampleData.builder.SampleDataBuilder

fun SampleDataBuilder.sampleData2() {
    val xi = user("Xi", AuthDigest("china")) {
        avatar("./test-resources/secret_avatar", ContentType.Image.JPEG)
    }
    val questionsList = listOf("1a", "1b", "1c", "1d", "2a", "2b", "2c")
    course("50009", "Symbolic Reasoning") {
        article("Paper_2022", xi) {
            defaultQuestions(questionsList)
            anonymous()
            question("1e") {
                content { "## Question blah blah blah..!" }
                anonymous()
                answer(xi) {
                    content("Haha, you found me!")
                }
            }
        }
    }
}