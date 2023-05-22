package org.solvo.model.utils

expect class PlatformDigest {
    fun md5(data: ByteArray): ByteArray

}

expect val Digest: PlatformDigest