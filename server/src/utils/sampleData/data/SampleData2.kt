package org.solvo.server.utils.sampleData.data

import org.solvo.server.modules.AuthDigest
import org.solvo.server.utils.sampleData.builder.SampleDataBuilder

fun SampleDataBuilder.sampleData2() {
    val charles = user("HiddenCharacter", AuthDigest("boo")) {}
    val questionsList = listOf("1a", "1b", "1c", "1d", "2a", "2b", "2c")
    course("50009", "Symbolic Reasoning") {
        article("Paper_2022", charles) {
            defaultQuestions(questionsList)
            anonymous()
            question("1e") {
                content { "## Question blah blah blah..!" }
                anonymous()
                answer(charles) {
                    content("Haha, you found me!")
                }
            }
        }
    }
}