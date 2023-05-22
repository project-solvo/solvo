/*
 * Copyright 2019-2022 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/dev/LICENSE
 */

package org.solvo.server.utils

import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.random.nextInt

fun getRandomByteArray(length: Int, random: Random = Random): ByteArray =
    ByteArray(length) { random.nextInt(0..255).toByte() }

fun getRandomUnsignedInt(): Int = Random.nextInt().absoluteValue

fun getRandomString(length: Int, random: Random = Random): String =
    getRandomString(length, *defaultRanges, random = random)

private val defaultRanges: Array<CharRange> = arrayOf('a'..'z', 'A'..'Z', '0'..'9')
private val intCharRanges: Array<CharRange> = arrayOf('0'..'9')

fun getRandomString(length: Int, charRange: CharRange, random: Random = Random): String =
    CharArray(length) { charRange.random(random) }.concatToString()

fun getRandomString(length: Int, vararg charRanges: CharRange, random: Random = Random): String =
    CharArray(length) { charRanges[random.nextInt(0..charRanges.lastIndex)].random(random) }.concatToString()


fun getRandomIntString(length: Int, random: Random = Random): String =
    getRandomString(length, charRanges = intCharRanges, random = random)
