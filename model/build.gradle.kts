@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

optInForAllSourceSets("kotlin.ExperimentalMultiplatform")

kotlin {
    jvm()
    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(`kotlinx-serialization-core`)
                api(`kotlinx-serialization-json`)
            }
        }
        val jvmMain by getting {
            dependencies {
                compileOnly(compose.runtime) // compose compiler requires this
            }
        }
        val jsMain by getting {
            dependencies {
                api(compose.runtime)
            }
        }
    }
}
