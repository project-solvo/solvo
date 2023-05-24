package web

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register
import rootProject
import java.io.File

@Suppress("PropertyName")
val WEBPACK_TASK_NAME = "jsBrowserDevelopmentWebpack"

@Suppress("PropertyName")
val DEVELOPMENT_EXECUTABLE = "developmentExecutable"


val indexHtmlFile get() = rootProject.project(":server").projectDir.resolve("resource-merger/static/index.html")
val indexHtmlContent = indexHtmlFile.readText()

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
            dependsOn(webCommon.tasks.getByName(WEBPACK_TASK_NAME))
            from(webCommon.buildDir.resolve(DEVELOPMENT_EXECUTABLE).resolve(staticResourceName))
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
    tasks.register("copyWebResources${destinationFilename.capitalized()}Html") {
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
        dependsOn(pageProject.tasks.getByName(WEBPACK_TASK_NAME))
        from(srcFile)
        rename { "$destinationFilename.js" }
        into(destinationDir)
    }.let {
        configureEach(it)
    }
}

fun disableWebProductionTasks(pageProject: Project) {
    pageProject.afterEvaluate {
        pageProject.tasks.getByName("jsProductionExecutableCompileSync").enabled = false
        pageProject.tasks.getByName("jsBrowserProductionWebpack").enabled = false
    }
}