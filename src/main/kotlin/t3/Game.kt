package t3

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import t3.io.OutputHandler
import t3.io.StdoutHandler
import t3.io.UserInputProvider
import t3.strategy.AlgorithmicStrategyProvider
import t3.strategy.StrategyProvider

@Serializable
class Game(
    private var board: Board = Board(),
    @Transient private var inputProvider: UserInputProvider = UserInputProvider(),
    @Transient private var outputHandler: OutputHandler = StdoutHandler(),
    @Transient private var strategyProvider: StrategyProvider = AlgorithmicStrategyProvider()
) {
    companion object {
        const val INTRO_MESSAGE_LINE_1 =
            "Welcome to Tic-Tac-Toe. Let's find out whether YOU are a bad enough " +
            "dude or dudette or non-binary dudx to beat the almighty computer."
        const val INTRO_MESSAGE_LINE_2 =
            "Make your play by entering the ID of the space you want to play in. " +
            "dude or dudette or non-binary dudx to beat the almighty computer."
        const val INTRO_MESSAGE_LINE_3 = "Let's gooooooo!!!!!!"
        const val PLAY_PROMPT = "What's your move?"
        const val REPLAY_PROMPT = "Play again (y/n)?"
        const val INVALID_INPUT_PROMPT = "Invalid input; try again."
        const val USER_WINS_MESSAGE =  "You did it! I'm so proud of you."
        const val COMPUTER_WINS_MESSAGE = "Bad news, computer wins. How could you let this happen?"
        const val DRAW_MESSAGE = "It's a draw!"
        fun fromJson(
            serialized: String,
            inputProvider: UserInputProvider? = null,
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
        outputHandler.writeln("${INTRO_MESSAGE_LINE_2}\n")
        outputHandler.writeln("${board.getEmptyLayout()}\n")
        outputHandler.writeln(INTRO_MESSAGE_LINE_3)
    }

    private fun promptForPlay() {
        outputHandler.write("${PLAY_PROMPT} ")
    }

    private fun makeUserPlay(): Boolean {
        return board.play(Player.USER, getSpaceChoice())
    }

    private fun makeComputerPlay(): Boolean {
        val spaceId = strategyProvider.getSpace(board)
        if (spaceId != null) {
            return board.play(Player.COMPUTER, spaceId)
        }
        return false
    }

    private fun promptForReplay() {
        outputHandler.write("${REPLAY_PROMPT} ")
    }

    private fun replayRequested(): Boolean {
        promptForReplay()
        if (getReplayChoice()) {
            board = Board()
            return true
        }
        return false
    }

    private fun getSpaceChoice(): Int {
        while (true) {
            val userInput = inputProvider.get()
            if (userInput != null && userInput.length > 0) {
                try {
                    val spaceId = userInput.toInt()
                    if (board.canPlay(spaceId)) {
                        return spaceId
                    }
                    outputHandler.write("Space ${spaceId} has already been played; try again. ")
                } catch (_: Throwable) {
                    outputHandler.write("${userInput} isn't a playable space; try again. ")
                }
            }
            promptForPlay()
        }
    }

    private fun getReplayChoice(): Boolean {
        while (true) {
            val userInput = inputProvider.get()
            if (userInput == "y") {
                return true
            }
            if (userInput == "n") {
                return false
            }
            outputHandler.write("${INVALID_INPUT_PROMPT} ")
            promptForReplay()
        }
    }

    fun playRound(): Player? {
        if (board.hasUnplayedSpaces() && makeUserPlay()) {
            return Player.USER
        }
        if (board.hasUnplayedSpaces() && makeComputerPlay()) {
            return Player.COMPUTER
        }
        if (!board.hasUnplayedSpaces()) {
            return Player.NONE
        }
        return null
    }

    fun run() {
        if (!board.hasPlayedSpaces()) {
            printWelcomeMessage()
        }
        var winner: Player?
        do {
            winner = null
            while (winner == null) {
                outputHandler.writeln("${board}\n")
                promptForPlay()
                winner = playRound()
                if (winner != null) {
                    when (winner) {
                        Player.USER -> outputHandler.write(
                            "${board}\n\n${USER_WINS_MESSAGE} "
                        )

                        Player.COMPUTER -> outputHandler.write(
                            "${board}\n\n${COMPUTER_WINS_MESSAGE} "
                        )

                        Player.NONE -> outputHandler.write(
                            "${board}\n\n${DRAW_MESSAGE} "
                        )
                    }
                }
            }
        } while (replayRequested())
    }

    fun toJson() = Json.encodeToString(this)
}