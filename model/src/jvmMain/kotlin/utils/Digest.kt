package org.solvo.model.utils

import java.security.MessageDigest


actual class PlatformDigest {
    actual fun md5(data: ByteArray): ByteArray {
        val d = MessageDigest.getInstance("md5")
        d.update(data)
        return d.digest()
    }
}

actual val Digest: PlatformDigest = PlatformDigest()
