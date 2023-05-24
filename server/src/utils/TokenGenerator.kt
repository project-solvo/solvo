package org.solvo.server.utils

import org.solvo.model.utils.getRandomString
import java.util.*

interface TokenGenerator {
    fun generateToken(userId: UUID): String
    fun matchToken(token: String): UUID?
    fun destroyToken(token: String): Boolean

    companion object {
        const val TOKEN_SIZE = 32
    }
}

class TokenGeneratorImpl: TokenGenerator {
    private val tokensMap = HashMap<String, UUID>()

    private fun randomTokenString(): String {
        var token: String
        do {
            token = getRandomString(TokenGenerator.TOKEN_SIZE)
        } while (tokensMap.keys.contains(token))
        return token
    }

    override fun generateToken(userId: UUID): String {
        return randomTokenString().also { tokensMap[it] = userId }
    }

    override fun matchToken(token: String): UUID? {
        return tokensMap[token]
    }

    override fun destroyToken(token: String): Boolean {
        return tokensMap.remove(token) != null
    }
}