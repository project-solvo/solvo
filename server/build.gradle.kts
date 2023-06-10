import web.*
import java.util.*

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

configureFlattenSourceSets()

dependencies {
    implementation(`exposed-core`)
    implementation(`exposed-dao`)
    implementation(`exposed-jdbc`)
    implementation(h2)
    implementation(`kotlinx-serialization-json`)
    implementation(`ktor-server-core`)
    implementation(`ktor-server-netty`)
    implementation(`ktor-server-call-logging`)
    implementation(`ktor-server-auth-jvm`)
    implementation(`ktor-server-core-jvm`)
    implementation(`ktor-server-websockets`)
    implementation(`ktor-server-content-negotiation`)
    implementation(`ktor-server-caching-headers`)
    implementation(`ktor-serialization-kotlinx-json`)
    implementation(`log4j2-core`)
    implementation(`log4j2-slf4j-impl`)
    implementation(`log4j-api-kotlin`)
    implementation(project(mapOf("path" to ":model")))
    implementation("io.ktor:ktor-server-core-jvm:2.3.0")
    implementation("io.ktor:ktor-server-websockets-jvm:2.3.0")
}

application {
    mainClass.set("org.solvo.server.ServerMain")
}

val pages: Map<String, String> by projectLevelCache {
    Properties().apply {
        project(":web").projectDir.resolve("web-pages.properties").bufferedReader().use {
            load(it)
        }
    }.mapKeys { it.key.toString().trim() }
        .mapValues { it.value.toString().trim() }
}

val destination = projectDir.resolve("resources/web-generated")

//        projectDir.resolve("resource-merger/static/styles.css")
//            .copyTo(destination.resolve("styles.css"))

val copyAllWebResources = tasks.register("copyAllWebResources")

registerCopyIndexPagesTasks(destination)


registerCopyStaticResourcesTasks(destination) {
    copyAllWebResources.get().dependsOn(it)
}

tasks.getByName("processResources").dependsOn(copyAllWebResources)

fun registerCopyIndexPagesTasks(destination: File) {
    for ((path, projectPath) in pages) {
        val pageProject = project(projectPath)
        val srcJsFile =
            pageProject.buildDir.resolve("${currentBuildType.executableDirName}/${projectPath.substringAfterLast(":")}.js")

        // Temp workaround for Compose bug
        disableWebProductionTasks(pageProject)

        // `$path.js`
        registerCopyWebResourceJsTask(path, pageProject, srcJsFile, destination) {
            copyAllWebResources.get().dependsOn(it)
        }
        // `index.html`
        registerCoopyWebResourceHtmlTask(path, destination, "$path.js") {
            copyAllWebResources.get().dependsOn(it)
        }
    }
}
