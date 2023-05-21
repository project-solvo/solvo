import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun Project.optInForAllSourceSets(qualifiedClassname: String) {
    kotlinSourceSetsOrNull?.all {
        languageSettings {
            optIn(qualifiedClassname)
        }
    }
}

fun Project.optInForTestSourceSets(qualifiedClassname: String) {
    kotlinSourceSetsOrNull?.matching { it.name.contains("test", ignoreCase = true) }?.all {
        languageSettings {
            optIn(qualifiedClassname)
        }
    }
}

fun Project.enableLanguageFeatureForAllSourceSets(qualifiedClassname: String) {
    kotlinSourceSetsOrNull?.all {
        languageSettings {
            this.enableLanguageFeature(qualifiedClassname)
        }
    }
}

fun Project.enableLanguageFeatureForTestSourceSets(name: String) {
    allKotlinTestSourceSets {
        languageSettings {
            this.enableLanguageFeature(name)
        }
    }
}

fun Project.allKotlinTestSourceSets(action: KotlinSourceSet.() -> Unit) {
    kotlinSourceSetsOrNull?.all {
        if (this.name.contains("test", ignoreCase = true)) {
            action()
        }
    }
}

fun Project.allKotlinSourceSets(action: KotlinSourceSet.() -> Unit) {
    kotlinSourceSetsOrNull?.all {
        action()
    }
}
