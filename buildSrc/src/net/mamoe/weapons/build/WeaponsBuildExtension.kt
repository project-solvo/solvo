package net.mamoe.weapons.build

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.getByType

open class WeaponsBuildExtension internal constructor(
    private val project: Project,
) {
    internal val jvmToolchainActions = mutableListOf<Action<JavaToolchainSpec>>()

    /**
     * Override JVM toolchain for the whole project
     */
    fun jvmToolchain(action: Action<JavaToolchainSpec>) {
        jvmToolchainActions.add(action)
    }

    /**
     * Override JVM toolchain for the whole project
     */
    fun jvmToolchain(version: JavaLanguageVersion) {
        jvmToolchain {
            languageVersion.set(version)
        }
    }

    /**
     * Override JVM toolchain for the whole project
     */
    fun jvmToolchain(version: Int) {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(version))
        }
    }

}

internal val Project.weaponsBuildExtension: WeaponsBuildExtension get() = extensions.getByType() 
