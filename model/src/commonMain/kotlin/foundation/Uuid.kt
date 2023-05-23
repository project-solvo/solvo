package org.solvo.model.foundation

import kotlinx.serialization.KSerializer

expect class Uuid

expect object UuidAsStringSerializer : KSerializer<Uuid>
