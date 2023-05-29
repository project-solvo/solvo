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
    companion object {
        fun fromString(string: String): Uuid = Uuid(string)
        fun random(): Uuid = Uuid(getRandomString(16))
    }
}

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