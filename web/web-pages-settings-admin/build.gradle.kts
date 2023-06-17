plugins {
    id("org.solvo.web-build")
}

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                api(project(":web:web-settings"))
            }
        }
    }
}
