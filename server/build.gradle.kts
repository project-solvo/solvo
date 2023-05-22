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
    implementation("io.ktor:ktor-server-auth-jvm:2.3.0")
    implementation("io.ktor:ktor-server-core-jvm:2.3.0")
    implementation(`ktor-server-content-negotiation`)
    implementation(`ktor-serialization-kotlinx-json`)
    implementation(project(mapOf("path" to ":model")))
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.0")
}

application {
    mainClass.set("org.solvo.server.ServerMain")
}