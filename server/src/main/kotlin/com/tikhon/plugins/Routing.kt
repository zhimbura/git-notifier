package com.tikhon.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello")
        }
        get("/health") {
            call.respondText("ok")
        }
    }
}
