package t3.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import t3.Context
import t3.Game
import t3.GameState
import t3.io.BufferedInputProvider
import t3.io.BufferedOutputHandler
import t3.serialization.Message
import t3.serialization.ServerMessage
import java.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/play") {
            try {
                val inputProvider = BufferedInputProvider()
                val outputHandler = BufferedOutputHandler()
                val game = Game(
                    context = Context.SOCKET,
                    inputProvider = inputProvider,
                    outputHandler = outputHandler
                )
                var state = game.advance()
                while (state != GameState.TERMINATED) {
                    sendSerialized(
                        ServerMessage(text = outputHandler.read(), game = game, state = state)
                    )
                    if (state == GameState.AWAITING_PLAY || state == GameState.AWAITING_PROMPT_RESPONSE) {
                        val userMessage = receiveDeserialized<Message>()
                        inputProvider.set(userMessage.text)
                    }
                    state = game.advance()
                }
            } catch (_: ClosedReceiveChannelException) {}
        }
    }
}