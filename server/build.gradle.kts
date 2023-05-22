plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

configureFlattenSourceSets()

dependencies {
    implementation(`exposed-core`)
    implementation(`exposed-dao`)
    implementation(`exposed-jdbc`)
    implementation(h2)
    implementation(`kotlinx-serialization-json`)
    implementation(`ktor-server-core`)
    implementation(`ktor-server-netty`)
    implementation(`ktor-server-call-logging`)
    implementation(`ktor-server-auth-jvm`)
    implementation(`ktor-server-core-jvm`)
    implementation(`ktor-server-content-negotiation`)
    implementation(`ktor-serialization-kotlinx-json`)
    implementation(`slf4j-api`)
    implementation(`slf4j-simple`)
    implementation(project(mapOf("path" to ":model")))
}

application {
    mainClass.set("org.solvo.server.ServerMain")
}