package org.prater

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.sessions.*
import org.prater.model.UserSession
import org.prater.plugins.*

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT").toInt(), host = "localhost") {
        install(Sessions) {
            cookie<UserSession>("user_session")
        }
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}
