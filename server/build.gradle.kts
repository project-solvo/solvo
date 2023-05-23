import org.gradle.configurationcache.extensions.capitalized

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
    "index" to ":web:web-pages-home",
)

val destination = projectDir.resolve("resources/web-generated")

//        projectDir.resolve("resource-merger/static/styles.css")
//            .copyTo(destination.resolve("styles.css"))

val indexHtmlFile = projectDir.resolve("resource-merger/static/index.html")
val indexHtmlContent = indexHtmlFile
    .readText()

val copyAllWebResources = tasks.register("copyAllWebResources")

for ((path, projectPath) in pages) {
    val pageProject = project(projectPath)
    val srcJsFile =
        pageProject.buildDir.resolve("developmentExecutable/${projectPath.substringAfterLast(":")}.js")
    val destJsFile = destination.resolve("$path.js")

    // Temp workaround for Compose bug
    pageProject.afterEvaluate {
        pageProject.tasks.getByName("jsProductionExecutableCompileSync").enabled = false
        pageProject.tasks.getByName("jsBrowserProductionWebpack").enabled = false
    }

    tasks.register("copyWebResources${path.capitalized()}Js", Copy::class) {
        dependsOn(pageProject.tasks.getByName("jsBrowserDevelopmentWebpack"))
        from(srcJsFile)
        rename { "$path.js" }
        into(destination)
    }.let {
        copyAllWebResources.get().dependsOn(it)
    }

    tasks.register("copyWebResources${path.capitalized()}Html") {
        val output = destination.resolve("$path.html")
        outputs.file(output)
        inputs.file(indexHtmlFile)
        inputs.property("dstJsFileName", destJsFile.name)

        val newContent = indexHtmlContent.replace("{{SCRIPT_PATH}}", destJsFile.name)

        doLast {
            output.writeText(newContent)
        }

        copyAllWebResources.get().dependsOn(this)
    }.let {
        copyAllWebResources.get().dependsOn(it)
    }

}

tasks.getByName("processResources").dependsOn(copyAllWebResources)