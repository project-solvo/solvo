package org.solvo.server.utils

import org.solvo.model.utils.getRandomString
import org.solvo.server.database.AccountDBFacade
import java.util.*

interface TokenGenerator {
    suspend fun generateToken(userId: UUID): String
    suspend fun matchToken(token: String): UUID?
    suspend fun destroyAllToken(userId: UUID): Boolean

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

    override suspend fun generateToken(userId: UUID): String {
        return randomTokenString().also { tokensMap[it] = userId }
    }

    override suspend fun matchToken(token: String): UUID? {
        return tokensMap[token]
    }

    override suspend fun destroyAllToken(userId: UUID): Boolean {
        tokensMap.forEach { (token, id) -> if (id == userId) tokensMap.remove(token, id) }
        return true
    }
}

class TokenGeneratorDBImpl(
    private val accounts: AccountDBFacade,
) : TokenGenerator {
    override suspend fun generateToken(userId: UUID): String {
        var token: String
        do {
            token = getRandomString(TokenGenerator.TOKEN_SIZE)
        } while (!accounts.addToken(userId, token))
        return token
    }

    override suspend fun matchToken(token: String): UUID? {
        return accounts.matchToken(token)
    }

    override suspend fun destroyAllToken(userId: UUID): Boolean {
        return accounts.removeAllTokens(userId)
    }
}
