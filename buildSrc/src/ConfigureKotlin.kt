import net.mamoe.weapons.build.weaponsBuildExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

val DEFAULT_JAVA_VERSION: JavaLanguageVersion = JavaLanguageVersion.of(11)


fun Project.configureJvmToolchain(version: JavaLanguageVersion = DEFAULT_JAVA_VERSION) {
    val actions = weaponsBuildExtension.jvmToolchainActions
    val javaExtension = javaExtensionOrNull
    val kotlinExtension = kotlinExtensionOrNull
    if (actions.isEmpty()) {
        kotlinExtension?.jvmToolchain { languageVersion.set(version) }
        javaExtension?.toolchain?.languageVersion?.set(version)
    } else {
        actions.forEach {
            kotlinExtension?.jvmToolchain(it)
            javaExtension?.toolchain(it)
        }
    }
}

private val Project.javaExtensionOrNull get() = extensions.findByType<JavaPluginExtension>()
private val Project.kotlinJvmExtensionOrNull get() = extensions.findByType<KotlinJvmProjectExtension>()
private val Project.kotlinExtensionOrNull get() = extensions.findByType<KotlinProjectExtension>()
private val Project.kotlinMppExtensionOrNull get() = extensions.findByType<KotlinMultiplatformExtension>()

fun Project.configureKotlinTest() {
    tasks.withType<Test> {
        useJUnitPlatform()
    }

    val kotlin = kotlinJvmExtensionOrNull ?: return
    val testSourceSet = kotlin.sourceSets.findByName("test") ?: return
    testSourceSet.dependencies {
        implementation(kotlin("test-junit5"))
    }
}


private val optIns = listOf(
    "kotlin.contracts.ExperimentalContracts",
    "kotlin.ExperimentalStdlibApi",
    "kotlin.io.path.ExperimentalPathApi",
    "androidx.compose.foundation.layout.ExperimentalLayoutApi",
    "androidx.compose.material3.ExperimentalMaterial3Api"
)

fun Project.configureCommonOptIns() {
    kotlinExtensionOrNull?.sourceSets?.all {
        languageSettings.languageVersion = Versions.kotlinLanguageVersion
        for (it in optIns) {
            languageSettings.optIn(it)
        }
    }

    enableLanguageFeatureForAllSourceSets("ContextReceivers")
}

fun Project.configureFlattenSourceSets() {
    val kotlin = kotlinExtension
    val main = kotlin.sourceSets.getByName("main")
    val test = kotlin.sourceSets.getByName("test")

    main.kotlin.srcDir(projectDir.resolve("src"))
    main.resources.srcDir(projectDir.resolve("resources"))
    test.kotlin.srcDir(projectDir.resolve("test"))
    test.resources.srcDir(projectDir.resolve("testResources"))
}