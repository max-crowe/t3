package t3.strategy

import t3.Board
import t3.Player
import t3.Space

class AlgorithmicStrategyProvider : StrategyProvider {
    override fun getSpace(board: Board): Int? {
        val waysToWin = board.getWaysToWin(Player.COMPUTER)
        if (waysToWin.isEmpty()) {
            return RandomStrategyProvider().getSpace(board)
        }
        val shortestWinPath = waysToWin.get(0).count()
        val opponentWaysToWin = board.getWaysToWin(Player.USER)
        val opponentShortestWinPath = opponentWaysToWin.get(0).count()
        val cohortWinPaths = waysToWin.filter { it.count() == shortestWinPath }
        val cohortOpponentWinPaths = opponentWaysToWin.filter { it.count() == opponentShortestWinPath }
        if (opponentShortestWinPath == 1 && opponentShortestWinPath < shortestWinPath && cohortOpponentWinPaths.count() == 1) {
            // Prioritize blocking the opponent
            return cohortOpponentWinPaths.first().first().id
        }
        val intersections = buildSet {
            for (winPath in cohortWinPaths) {
                for (opponentWinPath in cohortOpponentWinPaths) {
                    val intersection = winPath.intersect(opponentWinPath)
                    if (intersection.any()) {
                        add(intersection)
                    }
                }
            }
        }
        if (intersections.any()) {
            return intersections.shuffled().first().first().id
        }
        return waysToWin.first().first().id
    }
}