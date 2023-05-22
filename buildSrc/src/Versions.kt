@file:Suppress("ObjectPropertyName", "unused")

object Versions {
    const val kotlin = "1.8.20"
    const val kotlinLanguageVersion = "1.9"
    const val ktor = "2.3.0"
    const val atomicfu = "0.20.2"
    const val coroutines = "1.7.0"
    const val jna = "5.13.0"
    const val jsoup = "1.15.4"
    const val slf4j = "2.0.7"
    const val serialization = "1.5.0"
    const val compose = "1.5.0-dev1049"
}

const val `kotlinx-coroutines-core` = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
const val `kotlinx-coroutines-debug` = "org.jetbrains.kotlinx:kotlinx-coroutines-debug:${Versions.coroutines}"
const val `kotlinx-coroutines-test` = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"

const val `kotlinx-serialization-core` = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.serialization}"
const val `kotlinx-serialization-json` = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}"
const val `kotlinx-atomicfu` = "org.jetbrains.kotlin:kotlinx-atomicfu-runtime:${Versions.kotlin}"

const val `ktor-client-core` = "io.ktor:ktor-client-core:${Versions.ktor}"
const val `ktor-client-cio` = "io.ktor:ktor-client-cio:${Versions.ktor}"
const val `ktor-client-js` = "io.ktor:ktor-client-js:${Versions.ktor}"
const val `ktor-client-logging` = "io.ktor:ktor-client-logging:${Versions.ktor}"
const val `ktor-server-core` = "io.ktor:ktor-server-core:${Versions.ktor}"
const val `ktor-server-netty` = "io.ktor:ktor-server-netty:${Versions.ktor}"
const val `ktor-server-call-logging` = "io.ktor:ktor-server-call-logging:${Versions.ktor}"
const val `ktor-server-websockets` = "io.ktor:ktor-server-websockets:${Versions.ktor}"
const val `ktor-server-status-pages` = "io.ktor:ktor-server-status-pages:${Versions.ktor}"
const val `ktor-server-content-negotiation` = "io.ktor:ktor-server-content-negotiation:${Versions.ktor}"
const val `ktor-serialization-kotlinx-json` = "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}"
const val `ktor-server-cors` = "io.ktor:ktor-server-cors:${Versions.ktor}"
const val `ktor-io` = "io.ktor:ktor-io:${Versions.ktor}"
const val `ktor-utils` = "io.ktor:ktor-utils:${Versions.ktor}"
const val `ktor-network` = "io.ktor:ktor-network:${Versions.ktor}"
const val `ktor-http` = "io.ktor:ktor-http:${Versions.ktor}"
const val `ktor-client-websockets` = "io.ktor:ktor-client-websockets:${Versions.ktor}"

const val `slf4j-api` = "org.slf4j:slf4j-api:${Versions.slf4j}"
const val `slf4j-simple` = "org.slf4j:slf4j-simple:${Versions.slf4j}"
