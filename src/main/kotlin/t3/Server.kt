package t3

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import t3.plugins.configureRouting
import t3.plugins.configureSerialization
import t3.plugins.configureSockets

fun main() {
    embeddedServer(Netty, host = "0.0.0.0", port = 8080, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureSockets()
}