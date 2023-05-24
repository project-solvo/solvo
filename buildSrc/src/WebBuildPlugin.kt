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

class WebBuildPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        plugins.apply("org.jetbrains.kotlin.multiplatform")
        plugins.apply("org.jetbrains.compose")

        val resourcesGeneratedDir = buildDir.resolve("resources-generated")

        kotlinMultiplatformExtension.apply {
            js(IR) {
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

        tasks.register("copyWebResourcesStylesCss", Copy::class) {
            group = "solvo"

            from(project(":server").projectDir.resolve("resources/static/styles.css"))
            into(resourcesGeneratedDir)
        }.let {
            prepareTestEnvironmentResources.get().dependsOn(it)
        }

        disableWebProductionTasks(this)
//        afterEvaluate {
        tasks.getByName("jsProcessResources").dependsOn(prepareTestEnvironmentResources)
//        }
    }

}

internal val ComposeExtension.experimental get() = extensions.getByType(ExperimentalExtension::class)
internal val Project.compose get() = extensions.getByType(ComposeExtension::class)
internal val Project.kotlinMultiplatformExtension get() = kotlinExtension as KotlinMultiplatformExtension

