package web

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register
import projectLevelCache
import rootProject
import java.io.File

val currentBuildType by projectLevelCache {
    BuildType.valueOf(System.getProperty("solvo.build.type", BuildType.DEVELOPMENT.name))
}

enum class BuildType(
    val webpackTaskName: String,
    val executableDirName: String,
    val tasksToDisable: List<String>
) {
    DEVELOPMENT(
        "jsBrowserDevelopmentWebpack",
        "developmentExecutable",
        listOf("jsProductionExecutableCompileSync", "jsBrowserProductionWebpack")
    ),
    PRODUCTION(
        "jsBrowserProductionWebpack",
        "distributions",
        listOf("jsDevelopmentExecutableCompileSync", "jsBrowserDevelopmentWebpack")
    ),
}

val indexHtmlFile by projectLevelCache {
    rootProject.project(":server").projectDir.resolve("resource-merger/static/index.html")
}
val indexHtmlContent by projectLevelCache {
    indexHtmlFile.readText()
}

fun Project.registerCopyStaticResourcesTasks(destination: File, configureEach: (TaskProvider<Copy>) -> Unit = {}) {
    val staticResources = listOf(
        "skiko.js",
        "skiko.wasm",
    )

    val webCommon = project(":web:web-common")
    disableWebProductionTasks(webCommon)

    for (staticResourceName in staticResources) {
        val extension = staticResourceName.substringAfterLast(".")
        val capitalizedName =
            staticResourceName.substringBeforeLast(".").capitalized()
                .plus(extension.capitalized())

        tasks.register("copyWebResources$capitalizedName", Copy::class) {
            group = "solvo"
            dependsOn(webCommon.tasks.getByName(currentBuildType.webpackTaskName))
            from(webCommon.buildDir.resolve(currentBuildType.executableDirName).resolve(staticResourceName))
            into(destination)
        }.let {
            configureEach(it)
        }
    }
}

fun Project.registerCoopyWebResourceHtmlTask(
    destinationFilename: String,
    destination: File,
    jsAppFilePath: String,
    configureEach: (TaskProvider<*>) -> Unit = {}
) {
    tasks.register("copyWebResources_${destinationFilename}.html") {
        group = "solvo"
        val output = destination.resolve("$destinationFilename.html")
        outputs.file(output)
        inputs.file(indexHtmlFile)
        inputs.property("destinationFilename", jsAppFilePath)

        val newContent = indexHtmlContent.replace("{{SCRIPT_PATH}}", jsAppFilePath)

        doLast {
            output.writeText(newContent)
        }
    }.let {
        configureEach(it)
    }
}

fun Project.registerCopyWebResourceJsTask(
    destinationFilename: String,
    pageProject: Project,
    srcFile: File,
    destinationDir: File,
    configureEach: (TaskProvider<*>) -> Unit = {}
) {
    tasks.register("copyWebResources${destinationFilename.capitalized()}Js", Copy::class) {
        group = "solvo"
        dependsOn(pageProject.tasks.getByName(currentBuildType.webpackTaskName))
        from(srcFile)
        rename { "$destinationFilename.js" }
        into(destinationDir)
    }.let {
        configureEach(it)
    }
}

fun disableWebProductionTasks(pageProject: Project) {
    pageProject.afterEvaluate {
        currentBuildType.tasksToDisable.forEach { taskName ->
            pageProject.tasks.getByName(taskName).enabled = false
        }
    }
}