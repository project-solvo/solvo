package org.solvo.model.foundation

import kotlinx.serialization.KSerializer

expect class Uuid

expect fun randomUuid(): Uuid

expect object UuidAsStringSerializer : KSerializer<Uuid>
