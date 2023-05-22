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
                implementation(project(":model"))

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(`ktor-client-js`)
                implementation(`kotlinx-serialization-json`)
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

