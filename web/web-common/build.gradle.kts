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
                api(project(":model"))

                api(compose.runtime)
                api(compose.foundation)
                api(compose.materialIconsExtended)
                api(compose.ui)
                api(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                api(compose.components.resources)
                api(`ktor-client-js`)
                api(`ktor-client-content-negotiation`)
                api(`ktor-client-websockets`)
                api(`ktor-serialization-kotlinx-json`)
                api(`kotlinx-serialization-json`)
                api(`kotlinx-datetime`)
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

