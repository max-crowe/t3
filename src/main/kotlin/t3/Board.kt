package t3

class UnplayableSpaceException(message: String?) : IllegalArgumentException(message)

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

class Space(val id: Int) {
    var player = Player.NONE

    override fun toString(): String {
        return player.toString()
    }
}

class Board {
    val spaces = generateSpaces()
    var winner = Player.NONE
        private set

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
            // Diagonal
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
                        )
            ) {
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
}