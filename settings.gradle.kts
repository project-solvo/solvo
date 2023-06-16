rootProject.name = "solvo"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlinx-atomicfu") {
                useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
            }
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.4.0")
}

fun includeProject(projectPath: String, dir: String? = null) {
    include(projectPath)
    if (dir != null) project(projectPath).projectDir = file(dir)
}


// SHARED
includeProject(":model")

// BACKEND
includeProject(":server")

// WEB COMMON
includeProject(":web:web-common")
includeProject(":web:web-editor")
includeProject(":web:web-course-menu")
includeProject(":web:web-comments")
includeProject(":web:web-paging")
includeProject(":web:web-comment-column")

// WEB PAGES

includeProject(":web:web-pages-auth")
includeProject(":web:web-pages-question")
includeProject(":web:web-pages-home")
includeProject(":web:web-pages-course")
includeProject(":web:web-pages-user")
includeProject(":web:web-pages-admin")
