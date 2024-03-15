package t3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class CoordinateTest {
    @Test
    fun `fails when row or column out of range`() {
        assertThrows(
            IllegalArgumentException::class.java
        ) { Coordinate(-1, 0) }
        assertThrows(
            IllegalArgumentException::class.java
        ) { Coordinate(4, 0) }
        assertThrows(
            IllegalArgumentException::class.java
        ) { Coordinate(0, -1) }
        assertThrows(
            IllegalArgumentException::class.java
        ) { Coordinate(0, 4) }
    }

    @Test
    fun `space ID conversion fails when out of range`() {
        assertThrows(
            IllegalArgumentException::class.java
        ) { Coordinate.fromSpaceId(0) }
        assertThrows(
            IllegalArgumentException::class.java
        ) { Coordinate.fromSpaceId(-5) }
        assertThrows(
            IllegalArgumentException::class.java
        ) { Coordinate.fromSpaceId(10) }
    }

    @Test
    fun `equivalent coordinates compare as equal`() {
        assertTrue(Coordinate(0, 1) == Coordinate(0, 1))
        assertTrue(Coordinate(1, 2) == Coordinate(1, 2))
    }

    @Test
    fun `non-equivalent coordinates do not compare as equal`() {
        assertFalse(Coordinate(0, 1) == Coordinate(1, 2))
        assertFalse(Coordinate(1, 2) == Coordinate(2, 1))
    }
}

class BoardTest {
    @Test
    fun `prints empty layout`() {
        val board = Board()
        assertEquals(
            """
                1|2|3
                -----
                4|5|6
                -----
                7|8|9
            """.trimIndent(),
            board.getEmptyLayout()
        )
    }

    @Test
    fun `prints game layout`() {
        val board = Board()
        board.play(Player.USER, 2)
        board.play(Player.COMPUTER, 5)
        board.play(Player.USER, 9)
        assertEquals(
            """
                 |X| 
                -----
                 |O| 
                -----
                 | |X
            """.trimIndent(),
            board.toString()
        )
    }

    @Test
    fun `finds win in row`() {
        val board = Board()
        assertFalse(board.play(Player.USER, Coordinate(0, 0)))
        assertFalse(board.play(Player.USER, Coordinate(0, 2)))
        assertTrue(board.play(Player.USER, Coordinate(0, 1)))
        assertEquals(Player.USER, board.winner)
    }

    @Test
    fun `finds win in column`() {
        val board = Board()
        assertFalse(board.play(Player.COMPUTER, Coordinate(0, 0)))
        assertFalse(board.play(Player.COMPUTER, Coordinate(1, 0)))
        assertTrue(board.play(Player.COMPUTER, Coordinate(2, 0)))
        assertEquals(Player.COMPUTER, board.winner)
    }

    @Test
    fun `finds win in diagonal from top`() {
        val board = Board()
        assertFalse(board.play(Player.USER, Coordinate(0, 0)))
        assertFalse(board.play(Player.USER, Coordinate(2, 2)))
        assertTrue(board.play(Player.USER, Coordinate(1, 1)))
        assertEquals(Player.USER, board.winner)
    }

    @Test
    fun `finds win in diagonal from bottom`() {
        val board = Board()
        assertFalse(board.play(Player.COMPUTER, Coordinate(0, 2)))
        assertFalse(board.play(Player.COMPUTER, Coordinate(1, 1)))
        assertTrue(board.play(Player.COMPUTER, Coordinate(2, 0)))
        assertEquals(Player.COMPUTER, board.winner)
    }

    @Test
    fun `does not find win in row when players mixed`() {
        val board = Board()
        assertFalse(board.play(Player.COMPUTER, Coordinate(0, 0)))
        assertFalse(board.play(Player.USER, Coordinate(1, 0)))
        assertFalse(board.play(Player.COMPUTER, Coordinate(2, 0)))
        assertEquals(Player.NONE, board.winner)
    }

    @Test
    fun `does not find win in column when players mixed`() {
        val board = Board()
        assertFalse(board.play(Player.USER, Coordinate(0, 1)))
        assertFalse(board.play(Player.USER, Coordinate(1, 1)))
        assertFalse(board.play(Player.COMPUTER, Coordinate(2, 1)))
        assertEquals(Player.NONE, board.winner)
    }

    @Test
    fun `does not find win in diagonal from top when players mixed`() {
        val board = Board()
        assertFalse(board.play(Player.USER, Coordinate(0, 0)))
        assertFalse(board.play(Player.COMPUTER, Coordinate(2, 2)))
        assertFalse(board.play(Player.USER, Coordinate(1, 1)))
        assertEquals(Player.NONE, board.winner)
    }

    @Test
    fun `does not find win in diagonal from bottom when players mixed`() {
        val board = Board()
        assertFalse(board.play(Player.COMPUTER, Coordinate(0, 2)))
        assertFalse(board.play(Player.COMPUTER, Coordinate(1, 1)))
        assertFalse(board.play(Player.USER, Coordinate(2, 0)))
        assertEquals(Player.NONE, board.winner)
    }

    @Test
    fun `finds draw`() {
        val board = Board()
        assertFalse(board.play(Player.USER, Coordinate(0, 0)))
        assertFalse(board.play(Player.COMPUTER, Coordinate(0, 1)))
        assertFalse(board.play(Player.USER, Coordinate(1, 2)))
        assertFalse(board.play(Player.COMPUTER, Coordinate(2, 2)))
        assertFalse(board.play(Player.USER, Coordinate(2, 1)))
        assertFalse(board.play(Player.COMPUTER, Coordinate(1, 1)))
        assertFalse(board.play(Player.USER, Coordinate(0, 2)))
        assertFalse(board.play(Player.COMPUTER, Coordinate(2, 0)))
        assertFalse(board.play(Player.USER, Coordinate(1, 0)))
        assertFalse(board.hasUnplacedSpaces())
    }

    @Test
    fun `throws exception on attempt to replay space`() {
        val board = Board()
        board.play(Player.COMPUTER, Coordinate(0, 1))
        assertThrows(
            UnplayableSpaceException::class.java
        ) { board.play(Player.USER, Coordinate(0, 1)) }
    }

    @Test
    fun `finds unplayed spaces`() {
        val board = Board()
        var unplayedSpacesById: Sequence<Int>
        assertEquals(9, board.getUnplayedSpaces().count())
        board.play(Player.COMPUTER, 1)
        unplayedSpacesById = board.getUnplayedSpaces().map { it.id }
        assertEquals(8, unplayedSpacesById.count())
        assertFalse(unplayedSpacesById.contains(1))
        board.play(Player.USER, 8)
        unplayedSpacesById = board.getUnplayedSpaces().map { it.id }
        assertEquals(7, unplayedSpacesById.count())
        assertFalse(unplayedSpacesById.contains(1))
        assertFalse(unplayedSpacesById.contains(8))
    }

    @Test
    fun `spaces playable by ID`() {
        val board = Board()
        board.play(Player.COMPUTER, 2)
        assertThrows(UnplayableSpaceException::class.java) {
            board.play(Player.USER, Coordinate(0, 1))
        }
    }

    @Test
    fun `canPlay returns true for unplayed space`() {
        val board = Board()
        board.play(Player.USER, 4)
        assertTrue(board.canPlay(Coordinate(0, 0)))
    }

    @Test
    fun `canPlay returns false for played space`() {
        val board = Board()
        board.play(Player.USER, 4)
        assertFalse(board.canPlay(Coordinate(1, 0)))
    }

    @Test
    fun `all win possibilities found on empty board`() {
        assertEquals(8, Board().getWaysToWin(Player.USER).count())
    }

    @Test
    fun `win possibilities prioritized when board has one play`() {
        val board = Board()
        board.play(Player.USER, 1)
        val waysToWinById = board.getWaysToWin(Player.USER).map {
            it.map { it.id }
        }
        assertEquals(8, waysToWinById.count())
        assertEquals(listOf(2, 3), waysToWinById.get(0))
        assertEquals(listOf(4, 7), waysToWinById.get(1))
    }

    @Test
    fun `appropriate win possibilities excluded when board has plays from both players`() {
        val board = Board()
        board.play(Player.USER, 1)
        board.play(Player.COMPUTER, 3)
        var waysToWinById = board.getWaysToWin(Player.USER).map {
            it.map { it.id }
        }
        assertEquals(5, waysToWinById.count())
        assertFalse(waysToWinById.contains(listOf(2, 3)))
        assertEquals(listOf(4, 7), waysToWinById.get(0))
        waysToWinById = board.getWaysToWin(Player.COMPUTER).map {
            it.map { it.id }
        }
        assertEquals(5, waysToWinById.count())
        assertFalse(waysToWinById.contains(listOf(1, 2)))
        assertEquals(listOf(6, 9), waysToWinById.get(0))
    }
}