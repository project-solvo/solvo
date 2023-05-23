package org.solvo.model.foundation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import org.solvo.model.utils.getRandomString

@Serializable
actual class Uuid(
    val value: String
) {
    companion object {
        fun fromString(string: String): Uuid = Uuid(string)
        fun random(): Uuid = Uuid(getRandomString(16))
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Uuid::class)
actual object UuidAsStringSerializer : KSerializer<Uuid>