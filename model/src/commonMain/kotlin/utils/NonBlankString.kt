package org.solvo.model.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.solvo.model.annotations.Immutable
import kotlin.jvm.JvmInline

@JvmInline
@Immutable
@Serializable(NonBlankStringSerializer::class)
value class NonBlankString private constructor(
    val str: String
) {
    override fun toString(): String = str

    companion object {
        fun fromStringOrNull(str: String): NonBlankString? {
            if (str.isBlank()) return null
            return NonBlankString(str.trim())
        }

        fun fromString(str: String): NonBlankString = fromStringOrNull(str) ?: throw IllegalArgumentException(
            "Blank string passed to NonBlankString.fromString()"
        )
    }
}

inline val String.nonBlank: NonBlankString
    get() = NonBlankString.fromString(this)
inline val String.nonBlankOrNull: NonBlankString?
    get() = NonBlankString.fromStringOrNull(this)

internal object NonBlankStringSerializer : KSerializer<NonBlankString> {
    override val descriptor: SerialDescriptor
        get() = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): NonBlankString {
        val string = String.serializer().deserialize(decoder)
        return NonBlankString.fromStringOrNull(string) ?: throw SerializationException("NonBlankString cannot be blank")
    }

    override fun serialize(encoder: Encoder, value: NonBlankString) {
        return String.serializer().serialize(encoder, value.toString())
    }
}