package com.tikhon

import com.tikhon.app.ApplicationCore
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.tikhon.plugins.*

private val appCore = ApplicationCore()

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    webhooks(appCore)
    configureSerialization()
    configureRouting()
}
