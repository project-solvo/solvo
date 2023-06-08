package org.solvo.model.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.solvo.model.foundation.Uuid
import org.solvo.model.foundation.UuidAsStringSerializer


val DefaultCommonJson = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
    serializersModule = SerializersModule {
        contextual(Uuid::class, UuidAsStringSerializer)
    }
}