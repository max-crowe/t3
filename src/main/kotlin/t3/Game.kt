package t3

import java.lang.Exception
import java.util.Scanner

class Game {
    private var board = Board()
    private val scanner = Scanner(System.`in`)

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
        val unplayedSpaces = board.getUnplayedSpaces().shuffled()
        return board.play(Player.COMPUTER, unplayedSpaces.first().id)
    }

    private fun promptForReplay() {
        print("Play again (y/n)? ")
    }

    private fun replayRequested(): Boolean {
        promptForReplay()
        val choice = getReplayChoice()
        if (choice) {
            board = Board()
        }
        return choice
    }

    private fun getSpaceChoice(): Int {
        while (true) {
            val userInput = scanner.nextLine()
            try {
                val spaceId = userInput.toInt()
                if (board.canPlay(spaceId)) {
                    return spaceId
                }
                print("Space ${spaceId} has already been played; try again. ")
            } catch (_: Exception) {
                print("${userInput} isn't a number; try again. ")
            }
            promptForPlay()
        }
    }

    private fun getReplayChoice(): Boolean {
        while (true) {
            val userInput = scanner.nextLine().lowercase()
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
            while (board.winner == Player.NONE) {
                printBoardAndPrompt()
                if (makeUserPlay()) {
                    print("${board}\n\nYou did it! I'm so proud of you. ")
                } else if (makeComputerPlay()) {
                    print("${board}\n\nBad news, computer wins. How could you let this happen? ")
                }
            }
        } while (replayRequested())
    }
}