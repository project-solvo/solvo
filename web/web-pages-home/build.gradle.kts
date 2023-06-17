plugins {
    id("org.solvo.web-build")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(compose.material3)
                api(project(":web:web-settings"))
                api(project(":web:web-editor"))
                api(project(":web:web-pages-article-settings"))
            }
        }
    }
}

webResources {
    richTextEditor()
}