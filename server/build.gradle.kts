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


val copyWebResources = tasks.register("copyWebResources") {
    dependsOn(pages.values.map { tasks.getByPath(":$it:jsBrowserDevelopmentExecutableDistribution") })

    doLast {
        val destination = projectDir.resolve("resources/web-generated")

//        projectDir.resolve("resource-merger/static/styles.css")
//            .copyTo(destination.resolve("styles.css"))

        val index = projectDir.resolve("resource-merger/static/index.html")
            .readText()

        for ((path, projectPath) in pages) {
            val srcJsFile =
                project(projectPath).buildDir.resolve("developmentExecutable/${projectPath.substringAfterLast(":")}.js")
            val destJsFile = destination.resolve("$path.js")
            srcJsFile.copyTo(destJsFile, overwrite = true)
            destination.resolve("$path.html").writeText(index.replace("{{SCRIPT_PATH}}", destJsFile.name))
        }
    }
}

tasks.getByName("processResources").dependsOn(copyWebResources)