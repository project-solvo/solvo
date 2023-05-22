package org.solvo.model.utils

fun ByteArray.toHexString(
    separator: String = "",
    offset: Int = 0,
    length: Int = this.size - offset
): String {
    this.checkOffsetAndLength(offset, length)
    if (length == 0) {
        return ""
    }
    val lastIndex = offset + length
    return buildString(length * 2) {
        this@toHexString.forEachIndexed { index, it ->
            if (index in offset..<lastIndex) {
                val ret = it.toUByte().toString(16)
                if (ret.length == 1) append('0')
                append(ret)
                if (index < lastIndex - 1) append(separator)
            }
        }
    }
}

fun String.hexToBytes(): ByteArray {
    val array = ByteArray(countHexBytes())
    forEachHexChunkIndexed { index, char1, char2 ->
        array[index] = Byte.parseFromHexChunk(char1, char2)
    }
    return array
}


private fun ByteArray.checkOffsetAndLength(offset: Int, length: Int) {
    require(offset >= 0) { "offset shouldn't be negative: $offset" }
    require(length >= 0) { "length shouldn't be negative: $length" }
    require(offset + length <= this.size) { "offset ($offset) + length ($length) > array.size (${this.size})" }
}

fun Byte.Companion.parseFromHexChunk(char1: Char, char2: Char): Byte {
    return (char1.digitToInt(16).shl(SIZE_BITS / 2) or char2.digitToInt(16)).toByte()
}

private inline fun String.forEachHexChunkIndexed(block: (index: Int, char1: Char, char2: Char) -> Unit) {
    var index = 0
    forEachHexChunk { char1: Char, char2: Char ->
        block(index++, char1, char2)
    }
}

private inline fun String.forEachHexChunk(block: (char1: Char, char2: Char) -> Unit) {
    var chunkSize = 0
    var char1: Char = 0.toChar()
    for ((index, c) in this.withIndex()) { // compiler optimization
        if (c == ' ') {
            if (chunkSize != 0) {
                throw IllegalArgumentException("Invalid size of chunk at index ${index.minus(1)}")
            }
            continue
        }
        if (c in 'a'..'f' || c in 'A'..'F' || c in '0'..'9') { // compiler optimization
            when (chunkSize) {
                0 -> {
                    chunkSize = 1
                    char1 = c
                }

                1 -> {
                    block(char1, c)
                    chunkSize = 0
                }
            }
        } else {
            throw IllegalArgumentException("Invalid char '$c' at index $index")
        }
    }
    if (chunkSize != 0) {
        throw IllegalArgumentException("Invalid size of chunk at end of string")
    }
}


fun String.countHexBytes(): Int {
    var chunkSize = 0
    var count = 0
    for ((index, c) in this.withIndex()) {
        if (c == ' ') {
            if (chunkSize != 0) {
                throw IllegalArgumentException("Invalid size of chunk at index ${index.minus(1)}")
            }
            continue
        }
        if (c in 'a'..'f' || c in 'A'..'F' || c in '0'..'9') {
            when (chunkSize) {
                0 -> {
                    chunkSize = 1
                }

                1 -> {
                    count++
                    chunkSize = 0
                }
            }
        } else {
            throw IllegalArgumentException("Invalid char '$c' at index $index")
        }
    }
    if (chunkSize != 0) {
        throw IllegalArgumentException("Invalid size of chunk at end of string")
    }
    return count
}
