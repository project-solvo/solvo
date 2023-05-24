@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("org.solvo.web-build")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
//                implementation(npm("stackedit-js", "1.0.7"))
            }
        }
    }
}

webResources {
    richTextEditor()
}