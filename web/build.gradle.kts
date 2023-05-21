@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            kotlin.srcDir("src")
            resources.srcDir("resources")
            dependencies {
                implementation(project(":model"))
            }
        }
        val jsTest by getting {
            kotlin.srcDir("test")
            resources.srcDir("testResources")
        }
    }
}

compose.experimental {
    web.application {}
}

