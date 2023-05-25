plugins {
    id("org.solvo.web-build")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(project(":web:web-course-menu"))
            }
        }
    }
}