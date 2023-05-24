@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

plugins {
    id("org.solvo.web-build")
}

kotlin {
    targets.getByName("js", KotlinJsTargetDsl::class) {
        generateTypeScriptDefinitions()
    }
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