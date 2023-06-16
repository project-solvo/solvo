package org.solvo.web.requests

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.solvo.model.api.communication.AdminSettings
import org.solvo.model.foundation.Uuid

class SettingsRequests(
    override val client: Client,
) : Requests() {
    suspend fun getSettings(): AdminSettings {
        val resp = http.get(api("settings/admin")) {
            accountAuthorization()
        }
        return resp.body<AdminSettings>()
    }

    suspend fun setOperator(
        targetId: Uuid,
    ): Boolean {
        val resp = http.post(api("settings/admins/$targetId")) {
            accountAuthorization()
        }
        return resp.status.isSuccess()
    }

    suspend fun removeOperator(
        targetId: Uuid,
    ): Boolean {
        val resp = http.delete(api("settings/admins/$targetId")) {
            accountAuthorization()
        }
        return resp.status.isSuccess()
    }
}
