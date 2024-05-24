package t3

import t3.io.OutputHandler
import t3.io.StdoutHandler
import t3.io.UserInputProvider
import t3.strategy.AlgorithmicStrategyProvider
import t3.strategy.StrategyProvider

class Game(
    private val inputProvider: UserInputProvider = UserInputProvider(),
    private val outputHandler: OutputHandler = StdoutHandler(),
    private val strategyProvider: StrategyProvider = AlgorithmicStrategyProvider()
) {
    private var board = Board()

    private fun printWelcomeMessage() {
        outputHandler.writeln(
            "Welcome to Tic-Tac-Toe. Let's find out whether YOU are a bad enough " +
            "dude or dudette or non-binary dudx to beat the almighty computer.\n"
        )
        outputHandler.writeln(
            "Make your play by entering the ID of the space you want to play in. " +
            "Spaces are numbered 1 to 9, like this:\n"
        )
        outputHandler.writeln(board.getEmptyLayout())
        outputHandler.writeln("\nLet's gooooooo!!!!!!")
    }

    private fun promptForPlay() {
        outputHandler.write("What's your move? ")
    }

    private fun printBoardAndPrompt() {
        outputHandler.writeln("${board}\n")
        promptForPlay()
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
        outputHandler.write("Play again (y/n)? ")
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
            outputHandler.write("Invalid input; try again. ")
            promptForReplay()
        }
    }

    fun playRound(): Player? {
        if (board.hasUnplacedSpaces() && makeUserPlay()) {
            return Player.USER
        }
        if (board.hasUnplacedSpaces() && makeComputerPlay()) {
            return Player.COMPUTER
        }
        if (!board.hasUnplacedSpaces()) {
            return Player.NONE
        }
        return null
    }

    fun run() {
        printWelcomeMessage()
        var winner: Player?
        do {
            winner = null
            while (winner == null) {
                printBoardAndPrompt()
                winner = playRound()
                if (winner != null) {
                    when (winner) {
                        Player.USER -> outputHandler.write(
                            "${board}\n\nYou did it! I'm so proud of you. "
                        )

                        Player.COMPUTER -> outputHandler.write(
                            "${board}\n\nBad news, computer wins. How could you let this happen? "
                        )

                        Player.NONE -> outputHandler.write(
                            "${board}\n\nIt's a draw! "
                        )
                    }
                }
            }
        } while (replayRequested())
    }
}