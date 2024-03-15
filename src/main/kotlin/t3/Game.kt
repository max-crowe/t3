package t3

enum class ComputerStrategy {
    RANDOM, ALGORITHM
}

class Game(val computerStrategy: ComputerStrategy) {
    private var board = Board()

    private fun printWelcomeMessage() {
        println(
            "Welcome to Tic-Tac-Toe. Let's find out whether YOU are a bad enough " +
            "dude or dudette or non-binary dudx to beat the almighty computer.\n"
        )
        println(
            "Make your play by entering the ID of the space you want to play in. " +
            "Spaces are numbered 1 to 9, like this:\n"
        )
        println(board.getEmptyLayout())
        println("\nLet's gooooooo!!!!!!")
    }

    private fun promptForPlay() {
        print("What's your move? ")
    }

    private fun printBoardAndPrompt() {
        println("${board}\n")
        promptForPlay()
    }

    private fun makeUserPlay(): Boolean {
        return board.play(Player.USER, getSpaceChoice())
    }

    private fun makeComputerPlay(): Boolean {
        val waysToWin = board.getWaysToWin(Player.COMPUTER)
        if (computerStrategy == ComputerStrategy.RANDOM || waysToWin.count() == 0) {
            val unplayedSpaces = board.getUnplayedSpaces()
            if (unplayedSpaces.count() == 0) {
                return false
            }
            return board.play(Player.COMPUTER, unplayedSpaces.shuffled().first().id)
        }
        val opponentWaysToWin = board.getWaysToWin(Player.USER)
        val shortestWinPath = waysToWin.get(0).count()
        val cohortWinPaths = waysToWin.filter { it.count() == shortestWinPath }
        var intersection: Set<Space>
        for (winPath in cohortWinPaths) {
            for (opponentWinPath in opponentWaysToWin) {
                intersection = winPath.intersect(opponentWinPath)
                if (intersection.any()) {
                    return board.play(Player.COMPUTER, intersection.first().id)
                }
            }
        }
        return board.play(Player.COMPUTER, waysToWin.get(0).get(0).id)
    }

    private fun promptForReplay() {
        print("Play again (y/n)? ")
    }

    private fun replayRequested(): Boolean {
        promptForReplay()
        return getReplayChoice()
    }

    private fun getSpaceChoice(): Int {
        while (true) {
            val userInput = readlnOrNull()
            if (userInput != null && userInput.length > 0) {
                try {
                    val spaceId = userInput.toInt()
                    if (board.canPlay(spaceId)) {
                        return spaceId
                    }
                    print("Space ${spaceId} has already been played; try again. ")
                } catch (_: Throwable) {
                    print("${userInput} isn't a playable space; try again. ")
                }
            }
            promptForPlay()
        }
    }

    private fun getReplayChoice(): Boolean {
        while (true) {
            val userInput = readlnOrNull()
            if (userInput == "y") {
                return true
            }
            if (userInput == "n") {
                return false
            }
            print("Invalid input; try again. ")
            promptForReplay()
        }
    }

    fun run() {
        printWelcomeMessage()
        do {
            // Reset board if this is a replay
            if (board.winner != Player.NONE) {
                board = Board()
            }
            while (board.winner == Player.NONE && board.hasUnplacedSpaces()) {
                printBoardAndPrompt()
                if (makeUserPlay()) {
                    print("${board}\n\nYou did it! I'm so proud of you. ")
                } else if (makeComputerPlay()) {
                    print("${board}\n\nBad news, computer wins. How could you let this happen? ")
                } else if (!board.hasUnplacedSpaces()) {
                    print("${board}\n\nIt's a draw! ")
                }
            }
        } while (replayRequested())
    }
}