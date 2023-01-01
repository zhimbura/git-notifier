package com.tikhon.plugins

import com.tikhon.app.ApplicationCore
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureRouting() {

    routing {
        get("/") {
            val testApp = ApplicationCore()
            call.respondText("Projects: ${testApp.connect.getProjects().joinToString()}")
        }
    }
}
