import web.*

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
    implementation(`ktor-server-content-negotiation`)
    implementation(`ktor-serialization-kotlinx-json`)
    implementation(`slf4j-api`)
    implementation(`slf4j-simple`)
    implementation(project(mapOf("path" to ":model")))
}

application {
    mainClass.set("org.solvo.server.ServerMain")
}

val pages: Map<String, String> = mapOf(
    "register" to ":web:web-pages-register",
    "course" to ":web:web-pages-course",
    "index" to ":web:web-pages-home",
)

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
            pageProject.buildDir.resolve("$DEVELOPMENT_EXECUTABLE/${projectPath.substringAfterLast(":")}.js")

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
