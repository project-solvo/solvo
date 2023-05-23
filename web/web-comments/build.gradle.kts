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
        val commonMain by getting {
            dependencies {
            }
        }
        val jsMain by getting {
            kotlin.srcDir("src")
            resources.srcDir("resources")
            dependencies {
                api(project(":web:web-common"))
                api(compose.html.core)
                api(compose.html.svg)
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

