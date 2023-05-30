import kotlinx.atomicfu.plugin.gradle.AtomicFUPluginExtension
import net.mamoe.weapons.build.WeaponsBuildExtension

plugins {
    kotlin("jvm") apply false
    kotlin("plugin.serialization") version Versions.kotlin apply false
    id("kotlinx-atomicfu") apply false
    id("org.jetbrains.compose")
}

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
RootProjectHolder.setRootProject(project)

allprojects {
    group = "org.solvo"
    version = "1.0.0"

    extensions.create("weaponsBuild", WeaponsBuildExtension::class, this)

    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }

    afterEvaluate {
        if (this.name == "buildSrc") {
            return@afterEvaluate
        }
        configureJvmToolchain()
        configureKotlinTest()
        configureCommonOptIns()

        extensions.findByType<AtomicFUPluginExtension>()?.apply {
            transformJvm = false // bug
        }
    }
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}