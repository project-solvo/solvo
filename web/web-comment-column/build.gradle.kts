@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("org.solvo.web-build")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(project(":web:web-editor"))
            }
        }
    }
}

webResources {
    richTextEditor()
}