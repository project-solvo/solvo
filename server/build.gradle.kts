plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

configureFlattenSourceSets()

dependencies {
    api(`kotlinx-serialization-json`)
}
