package t3.serialization

import kotlinx.serialization.Serializable
import t3.Game
import t3.GameState

@Serializable
data class ServerMessage(val text: String?, val game: Game, val state: GameState)
