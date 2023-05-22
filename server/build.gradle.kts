plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

configureFlattenSourceSets()

dependencies {
    implementation(`kotlinx-serialization-json`)
    implementation(`ktor-server-core`)
    implementation(`ktor-server-netty`)
    implementation(`ktor-server-call-logging`)
    implementation(`slf4j-api`)
    implementation(`slf4j-simple`)
    implementation(`ktor-server-content-negotiation`)
    implementation(`ktor-serialization-kotlinx-json`)
    implementation(project(mapOf("path" to ":model")))
}

application {
    mainClass.set("org.solvo.server.ServerMain")
}