package t3.strategy

import t3.Board

interface StrategyProvider {
    fun getSpace(board: Board): Int?
}