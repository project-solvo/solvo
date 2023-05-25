import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.experimental.dsl.ExperimentalExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import web.disableWebProductionTasks
import web.registerCoopyWebResourceHtmlTask
import java.io.File

val Project.resourcesGeneratedDir get() = buildDir.resolve("resources-generated").apply { mkdirs() }

class WebBuildPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        plugins.apply("org.jetbrains.kotlin.multiplatform")
        plugins.apply("org.jetbrains.compose")

        kotlinMultiplatformExtension.apply {
            js(IR) {
                moduleName = project.name
                browser()
                binaries.executable()
            }
            sourceSets.apply {
                getByName("jsMain") {
                    kotlin.setSrcDirs(listOf("src"))
                    resources.srcDir(listOf("resources", resourcesGeneratedDir))
                    dependencies {
                        implementation(project(":web:web-common"))
                    }
                }
                getByName("jsTest") {
                    kotlin.setSrcDirs(listOf("test"))
                    resources.srcDir(listOf("testResources"))
                }
            }
        }
        compose.experimental.apply {
            web.application {}
        }

        val prepareTestEnvironmentResources = tasks.register("prepareTestEnvironmentResources", Copy::class) {
            group = "solvo"
        }

        registerCoopyWebResourceHtmlTask(
            "index",
            resourcesGeneratedDir,
            project.name + ".js"
        ) {
            prepareTestEnvironmentResources.get().dependsOn(it)
        }

        registerCopyWebStaticResourceTask("styles.css", resourcesGeneratedDir)
        registerCopyWebStaticResourceTask("fonts", resourcesGeneratedDir.resolve("fonts"))

        disableWebProductionTasks(this)
//        afterEvaluate {
        tasks.getByName("jsProcessResources").dependsOn(prepareTestEnvironmentResources)
//        }
    }

}

internal val ComposeExtension.experimental get() = extensions.getByType(ExperimentalExtension::class)
internal val Project.compose get() = extensions.getByType(ComposeExtension::class)
internal val Project.kotlinMultiplatformExtension get() = kotlinExtension as KotlinMultiplatformExtension


fun Project.registerCopyWebStaticResourceTask(
    filepath: String,
    destinationDir: File = resourcesGeneratedDir,
) {
//    val taskSuffix = filepath.substrin??gBeforeLast(".").replace(".", "").capitalized()
//        .plus(filepath.substringAfterLast(".").capitalized())

    tasks.register("copyWebResources_${filepath}", Copy::class) {
        group = "solvo"

        val source = project(":server").projectDir.resolve("resources/static/$filepath")
        if (!source.exists()) {
            throw IllegalStateException("Web resource does not exist: ${source.absolutePath}")
        }
        from(source)
        into(destinationDir)
    }.let {
        tasks.getByName("prepareTestEnvironmentResources").dependsOn(it)
    }
}
