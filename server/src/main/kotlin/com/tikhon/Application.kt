package com.tikhon

import com.tikhon.app.ApplicationCore
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.tikhon.plugins.*

private val appCore = ApplicationCore()

private val PORT = System.getenv("APP_PORT").toInt()
private val HOST = System.getenv("APP_HOST")

fun main() {
    embeddedServer(CIO, port = PORT, host = HOST, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    webhooks(appCore)
    configureSerialization()
    configureRouting()
}
