package t3

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UnplayableSpaceException(message: String?) : IllegalArgumentException(message)
class UnwinnablePossibilityException(message: String?): RuntimeException(message)

private fun generateSpaces(): List<List<Space>> {
    var position = 0
    return listOf(
        listOf(Space(++position), Space(++position), Space(++position)),
        listOf(Space(++position), Space(++position), Space(++position)),
        listOf(Space(++position), Space(++position), Space(++position))
    )
}

private fun getReadableLayout(spaces: List<List<Any>>): String {
    return """
        ${spaces[0].joinToString("|")}
        -----
        ${spaces[1].joinToString("|")}
        -----
        ${spaces[2].joinToString("|")}
    """.trimIndent()
}

class Coordinate(val row: Int, val column: Int) {
    init {
        require(row in 0..2) {
            "Row must be between 0 and 2"
        }
        require(column in 0..2) {
            "Column must be between 0 and 2"
        }
    }

    companion object {
        fun fromSpaceId(spaceId: Int): Coordinate {
            require(spaceId in 1..9) {
                "Space ID must be between 1 and 9"
            }
            val spaceIdOffset = spaceId - 1
            return Coordinate(
                row = spaceIdOffset.floorDiv(3),
                column = spaceIdOffset % 3
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is Coordinate) {
            return row == other.row && column == other.column
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return listOf(row, column).hashCode()
    }
}

@Serializable
class Space(val id: Int, var player: Player = Player.NONE) {
    override fun toString(): String {
        return player.toString()
    }
}

@Serializable
class Board(val spaces: List<List<Space>> = generateSpaces()) {
    var winner = Player.NONE
        private set
    private var unplayedSpaceCount = getUnplayedSpaces().count()

    companion object {
        fun fromJson(serialized: String) = Json.decodeFromString<Board>(serialized)
    }

    override fun toString(): String {
        return getReadableLayout(spaces)
    }

    fun getEmptyLayout(): String {
        return getReadableLayout(spaces.map { it.map { it.id } })
    }

    private fun checkForWin(coordinate: Coordinate): Boolean {
        val observedPlayer = spaces[coordinate.row][coordinate.column].player
        if (observedPlayer != Player.NONE) {
            // Horizontal
            for (idx in 0..2) {
                if (spaces[coordinate.row][idx].player != observedPlayer) {
                    break
                }
                if (idx == 2) {
                    winner = observedPlayer
                }
            }
            // Vertical
            if (winner == Player.NONE) {
                for (idx in 0..2) {
                    if (spaces[idx][coordinate.column].player != observedPlayer) {
                        break
                    }
                    if (idx == 2) {
                        winner = observedPlayer
                    }
                }
            }
            // Only necessary to check for diagonals if certain spaces are played
            if (winner == Player.NONE && (
                (coordinate.row == 1 && coordinate.column == 1) || (
                    coordinate.row != 1 && coordinate.column != 1
                )
            )) {
                // Diagonal from top left
                for (idx in 0..2) {
                    if (spaces[idx][idx].player != observedPlayer) {
                        break
                    }
                    if (idx == 2) {
                        winner = observedPlayer
                    }
                }
                // Diagonal from bottom left
                if (winner == Player.NONE) {
                    for (idx in 0..2) {
                        if (spaces[2 - idx][idx].player != observedPlayer) {
                            break
                        }
                        if (idx == 2) {
                            winner = observedPlayer
                        }
                    }
                }
            }
        }
        return winner != Player.NONE
    }

    private fun addSpaceToWinPossibility(
        player: Player, wayToWin: MutableList<Space>, coordinate: Coordinate
    ) {
        val space = spaces[coordinate.row][coordinate.column]
        if (space.player == Player.NONE) {
            wayToWin.add(space)
        } else if (space.player != player) {
            throw UnwinnablePossibilityException("Player blocked from winning here")
        }
    }

    fun canPlay(coordinate: Coordinate): Boolean {
        return spaces[coordinate.row][coordinate.column].player == Player.NONE
    }

    fun canPlay(spaceId: Int): Boolean {
        return canPlay(Coordinate.fromSpaceId(spaceId))
    }

    fun play(player: Player, coordinate: Coordinate): Boolean {
        if (!canPlay(coordinate)) {
            throw UnplayableSpaceException("You cannot replay that space")
        }
        spaces[coordinate.row][coordinate.column].player = player
        unplayedSpaceCount--
        return checkForWin(coordinate)
    }

    fun play(player: Player, spaceId: Int): Boolean {
        return play(player, Coordinate.fromSpaceId(spaceId))
    }

    fun getUnplayedSpaces(): Sequence<Space> {
        return sequence {
            for (rowIdx in 0..2) {
                yieldAll(spaces[rowIdx].filter { it.player == Player.NONE })
            }
        }
    }

    fun hasUnplayedSpaces() = unplayedSpaceCount > 0

    fun hasPlayedSpaces() = unplayedSpaceCount < 9

    fun getWaysToWin(player: Player): List<List<Space>> {
        val waysToWin = mutableListOf<List<Space>>()
        var wayToWin: MutableList<Space>
        // Horizontal
        for (rowIdx in 0..2) {
            wayToWin = mutableListOf()
            try {
                for (colIdx in 0..2) {
                    addSpaceToWinPossibility(
                        player, wayToWin, Coordinate(rowIdx, colIdx)
                    )
                }
                if (wayToWin.count() > 0) {
                    waysToWin.add(wayToWin)
                }
            } catch (_: UnwinnablePossibilityException) {}
        }
        // Vertical
        for (colIdx in 0..2) {
            wayToWin = mutableListOf()
            try {
                for (rowIdx in 0..2) {
                    addSpaceToWinPossibility(
                        player, wayToWin, Coordinate(rowIdx, colIdx)
                    )
                }
                if (wayToWin.count() > 0) {
                    waysToWin.add(wayToWin)
                }
            } catch (_: UnwinnablePossibilityException) {}
        }
        // Diagonal from top left
        try {
            wayToWin = mutableListOf()
            for (idx in 0..2) {
                addSpaceToWinPossibility(
                    player, wayToWin, Coordinate(idx, idx)
                )
            }
            if (wayToWin.count() > 0) {
                waysToWin.add(wayToWin)
            }
        } catch (_: UnwinnablePossibilityException) {}
        // Diagonal from bottom left
        try {
            wayToWin = mutableListOf()
            for (idx in 0..2) {
                addSpaceToWinPossibility(
                    player, wayToWin, Coordinate(2 - idx, idx)
                )
            }
            if (wayToWin.count() > 0) {
                waysToWin.add(wayToWin)
            }
        } catch (_: UnwinnablePossibilityException) {}
        // Prioritize by length
        waysToWin.sortBy { it.count() }
        return waysToWin
    }

    fun toJson() = Json.encodeToString(this)
}