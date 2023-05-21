plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

sourceSets.main.get().kotlin.srcDir("src")

val versionCache: Map<String, String> by lazy {
    val versions = rootProject.rootDir.resolve("src/Versions.kt").readText()

    // const val kotlin = "1.8.0"
    Regex("""const val \s?`?([a-zA-Z0-9_\-=+.${'$'}]+)`?\s?=\s?"(.*?)"""").findAll(versions)
        .map {
            it.destructured
        }
        .map { (name, value) ->
            name to value
        }
        .toMap()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-gradle-plugin-api:${version("kotlin")}")
    api("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${version("atomicfu")}")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:${version("kotlin")}")
    api(gradleApi())
    api(gradleKotlinDsl())
}

kotlin.sourceSets.all {
    languageSettings.optIn("org.jetbrains.compose.ExperimentalComposeLibrary")
}


fun version(name: String): String {
    return versionCache[name] ?: throw GradleException("Cannot find version '$name'")
}