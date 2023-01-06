package com.tikhon.plugins

import com.tikhon.app.ApplicationCore
import com.tikhon.app.adapters.git.gitlab.GitLabAdapter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Application.webhooks(appCore: ApplicationCore) {

    routing {
        post("/webhooks/gitlab") {
            val bodyRequest: String = call.receiveText()
            val event = GitLabAdapter.parseEvent(bodyRequest)
            if (event != null) {
                appCore.receiveEvent(event)
                call.response.status(HttpStatusCode.OK)
            } else {
                call.response.status(HttpStatusCode.NotFound)
            }
        }
    }
}