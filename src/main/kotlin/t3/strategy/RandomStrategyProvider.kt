package t3.strategy

import t3.Board

class RandomStrategyProvider : StrategyProvider {
    override fun getSpace(board: Board): Int? {
        val firstUnplayedSpace = board.getUnplayedSpaces().shuffled().firstOrNull()
        return firstUnplayedSpace?.id
    }
}