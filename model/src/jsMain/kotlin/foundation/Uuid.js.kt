package org.solvo.model.foundation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.solvo.model.utils.getRandomString

@Serializable
actual class Uuid(
    val value: String
) {
    override fun toString(): String = value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Uuid

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }


    companion object {
        fun fromString(string: String): Uuid = Uuid(string)
        fun random(): Uuid = Uuid(getRandomString(16))
    }
}

actual fun randomUuid(): Uuid = Uuid.random()

actual object UuidAsStringSerializer : KSerializer<Uuid> {
    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): Uuid {
        val string = String.serializer().deserialize(decoder)
        return try {
            Uuid.fromString(string)
        } catch (e: IllegalArgumentException) {
            throw SerializationException("Failed to deserialize UUID: $string", e)
        }
    }

    override fun serialize(encoder: Encoder, value: Uuid) {
        String.serializer().serialize(encoder, value.toString())
    }
}