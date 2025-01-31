package t3

import io.ktor.util.logging.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import t3.io.InputProvider
import t3.io.OutputHandler
import t3.io.StdoutHandler
import t3.io.StdinProvider
import t3.strategy.AlgorithmicStrategyProvider
import t3.strategy.StrategyProvider

internal val LOGGER = KtorSimpleLogger("t3.Game")

class EmptyInputException: Exception("Input was unexpectedly empty")
class InvalidInputException(message: String): Exception(message)
class SpaceOverflowException(message: String): Exception(message)

@Serializable
class Game(
    private var board: Board = Board(),
    private val context: Context = Context.TTY,
    @Transient private var inputProvider: InputProvider = StdinProvider(),
    @Transient private var outputHandler: OutputHandler = StdoutHandler(),
    @Transient private var strategyProvider: StrategyProvider = AlgorithmicStrategyProvider()
) {
    private var state: GameState = GameState.INITIATED

    companion object {
        const val INTRO_MESSAGE_LINE_1 =
            "Welcome to Tic-Tac-Toe. Let's find out whether YOU are a bad enough " +
            "dude or dudette or non-binary dudx to beat the almighty computer."
        const val INTRO_MESSAGE_LINE_2 =
            "Make your play by entering the ID of the space you want to play in."
        const val INTRO_MESSAGE_LINE_3 = "Let's gooooooo!!!!!!"
        const val PLAY_PROMPT = "What's your move?"
        const val REPLAY_PROMPT = "Play again (y/n)?"
        const val INVALID_INPUT_PROMPT = "Invalid input; try again."
        const val USER_WINS_MESSAGE =  "You did it! I'm so proud of you."
        const val COMPUTER_WINS_MESSAGE = "Bad news, computer wins. How could you let this happen?"
        const val DRAW_MESSAGE = "It's a draw!"

        fun fromJson(
            serialized: String,
            inputProvider: StdinProvider? = null,
            outputHandler: OutputHandler? = null,
            strategyProvider: StrategyProvider? = null
        ): Game {
            val game = Json.decodeFromString<Game>(serialized)
            if (inputProvider != null) {
                game.inputProvider = inputProvider
            }
            if (outputHandler != null) {
                game.outputHandler = outputHandler
            }
            if (strategyProvider != null) {
                game.strategyProvider = strategyProvider
            }
            return game
        }
    }

    private fun printWelcomeMessage() {
        outputHandler.writeln("${INTRO_MESSAGE_LINE_1}\n")
        if (context == Context.TTY) {
            outputHandler.writeln("${INTRO_MESSAGE_LINE_2}\n")
            outputHandler.writeln("${board.getEmptyLayout()}\n")
        }
        outputHandler.writeln(INTRO_MESSAGE_LINE_3)
    }

    private fun promptForPlay(printBoard: Boolean = true) {
        if (printBoard && context == Context.TTY) {
            outputHandler.writeln(board)
        }
        outputHandler.write("$PLAY_PROMPT ")
    }

    private fun makeUserPlay(): Boolean {
        val spaceId = getSpaceChoice()
        return board.play(Player.USER, spaceId)
    }

    private fun makeComputerPlay(): Boolean {
        val spaceId = strategyProvider.getSpace(board)
        if (spaceId != null) {
            return board.play(Player.COMPUTER, spaceId)
        }
        return false
    }

    private fun promptForReplay() {
        outputHandler.write("$REPLAY_PROMPT ")
    }

    private fun getSpaceChoice(): Int {
        val userInput = inputProvider.get()
        if (userInput.isNullOrEmpty()) {
            throw EmptyInputException()
        }
        LOGGER.debug("Received user input: {}", userInput)
        val spaceId: Int
        try {
            spaceId = userInput.toInt()
        } catch (_: Throwable) {
            throw InvalidInputException("$userInput isn't a playable space")
        }
        if (!board.canPlay(spaceId)) {
            throw SpaceOverflowException("Space $spaceId has already been played")
        }
        return spaceId
    }

    private fun getReplayChoice(): Boolean {
        val userInput = inputProvider.get()
        if (userInput == "y") {
            return true
        }
        if (userInput == "n") {
            return false
        }
        throw InvalidInputException("Invalid input: $userInput")
    }

    fun playRound(): Player? {
        if (board.hasUnplayedSpaces() && makeUserPlay()) {
            return Player.USER
        } else if (board.hasUnplayedSpaces() && makeComputerPlay()) {
            return Player.COMPUTER
        } else if (!board.hasUnplayedSpaces()) {
            return Player.NONE
        }
        return null
    }

    fun advance(): GameState {
        when (state) {
            GameState.AWAITING_PROMPT_RESPONSE -> {
                try {
                    if (getReplayChoice()) {
                        board = Board()
                        state = GameState.AWAITING_PLAY
                        promptForPlay()
                    } else {
                        state = GameState.TERMINATED
                    }
                } catch (_: InvalidInputException) {
                    outputHandler.write("$INVALID_INPUT_PROMPT ")
                    promptForReplay()
                }
            }

            GameState.AWAITING_PLAY -> {
                try {
                    val winner = playRound()
                    if (winner == null) {
                        promptForPlay()
                    } else {
                        if (context == Context.TTY) {
                            outputHandler.writeln(board)
                        }
                        when (winner) {
                            Player.USER -> outputHandler.write(
                                "$USER_WINS_MESSAGE "
                            )

                            Player.COMPUTER -> outputHandler.write(
                                "$COMPUTER_WINS_MESSAGE "
                            )

                            Player.NONE -> outputHandler.write(
                                "$DRAW_MESSAGE "
                            )
                        }
                        promptForReplay()
                        state = GameState.AWAITING_PROMPT_RESPONSE
                    }
                } catch (e: SpaceOverflowException) {
                    outputHandler.write("${e.message}; try again\n")
                    promptForPlay(printBoard = false)
                } catch (e: InvalidInputException) {
                    outputHandler.write("${e.message}; try again\n")
                    promptForPlay(printBoard = false)
                }
            }

            GameState.INITIATED -> {
                printWelcomeMessage()
                promptForPlay()
                state = GameState.AWAITING_PLAY
            }

            GameState.TERMINATED -> {}
        }
        return state
    }

    fun run() {
        while (true) {
            if (advance() == GameState.TERMINATED) {
                return
            }
        }
    }

    fun toJson() = Json.encodeToString(this)
}