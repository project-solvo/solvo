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

// WEB PAGES

includeProject(":web:web-pages-auth")
includeProject(":web:web-pages-article")
includeProject(":web:web-pages-home")
includeProject(":web:web-pages-course")
