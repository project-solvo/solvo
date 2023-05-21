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
}

application {
    mainClass.set("org.solvo.server.ServerMain")
}