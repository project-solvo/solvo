package org.solvo.server

import org.solvo.server.utils.getRandomString

interface TokenGenerator {
    fun generateToken(userId: Int): String
    fun matchToken(token: String): Int?
    fun destroyToken(token: String): Boolean

    companion object {
        const val TOKEN_SIZE = 32
    }
}

class TokenGeneratorImpl: TokenGenerator {
    private val tokensMap = HashMap<String, Int>()

    private fun randomTokenString(): String {
        var token: String
        do {
            token = getRandomString(TokenGenerator.TOKEN_SIZE)
        } while (tokensMap.keys.contains(token))
        return token
    }

    override fun generateToken(userId: Int): String {
        return randomTokenString().also { tokensMap[it] = userId }
    }

    override fun matchToken(token: String): Int? {
        return tokensMap[token]
    }

    override fun destroyToken(token: String): Boolean {
        return tokensMap.remove(token) != null
    }
}