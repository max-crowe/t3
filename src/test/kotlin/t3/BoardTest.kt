package t3

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.sequences.shouldHaveSize
import io.kotest.matchers.sequences.shouldNotContain
import io.kotest.matchers.shouldBe

class CoordinateTest : FunSpec({
    test("fails when row or column out of range") {
        shouldThrow<IllegalArgumentException> {
            Coordinate(-1, 0)
        }
        shouldThrow<IllegalArgumentException> {
            Coordinate(4, 0)
        }
        shouldThrow<IllegalArgumentException> {
            Coordinate(0, -1)
        }
        shouldThrow<IllegalArgumentException> {
            Coordinate(0, -1)
        }
        shouldThrow<IllegalArgumentException> {
            Coordinate(0, 4)
        }
    }

    test("space ID conversion fails when out of range") {
        shouldThrow<IllegalArgumentException> {
            Coordinate.fromSpaceId(0)
        }
        shouldThrow<IllegalArgumentException> {
            Coordinate.fromSpaceId(-5)
        }
        shouldThrow<IllegalArgumentException> {
            Coordinate.fromSpaceId(10)
        }
    }

    test("equivalent coordinates compare as equal") {
        Coordinate(0, 1) shouldBeEqual Coordinate(0, 1)
        Coordinate(1, 2) shouldBeEqual Coordinate(1, 2)
    }

    test("non-equivalent coordinates do not compare as equal") {
        Coordinate(0, 1) shouldNotBeEqual Coordinate(1, 2)
        Coordinate(1, 2) shouldNotBeEqual Coordinate(2, 1)
    }
})

class BoardTest : FunSpec({
    test("prints empty layout") {
        val board = Board()
        board.getEmptyLayout() shouldBe """
            1|2|3
            -----
            4|5|6
            -----
            7|8|9
        """.trimIndent()
    }

    test("prints game layout") {
        val board = Board()
        board.play(Player.USER, 2)
        board.play(Player.COMPUTER, 5)
        board.play(Player.USER, 9)
        board.toString() shouldBe """
             |X| 
            -----
             |O| 
            -----
             | |X
        """.trimIndent()
    }

    test("finds win in row") {
        val board = Board()
        board.play(Player.USER, Coordinate(0, 0)) shouldBe false
        board.play(Player.USER, Coordinate(0, 2)) shouldBe false
        board.play(Player.USER, Coordinate(0, 1)) shouldBe true
        board.winner shouldBe Player.USER
    }

    test("finds win in column") {
        val board = Board()
        board.play(Player.COMPUTER, Coordinate(0, 0)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(1, 0)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(2, 0)) shouldBe true
        board.winner shouldBe Player.COMPUTER
    }

    test("finds win in diagonal from top") {
        val board = Board()
        board.play(Player.USER, Coordinate(0, 0)) shouldBe false
        board.play(Player.USER, Coordinate(2, 2)) shouldBe false
        board.play(Player.USER, Coordinate(1, 1)) shouldBe true
        board.winner shouldBe Player.USER
    }

    test("finds win in diagonal from bottom") {
        val board = Board()
        board.play(Player.COMPUTER, Coordinate(0, 2)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(1, 1)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(2, 0)) shouldBe true
        board.winner shouldBe Player.COMPUTER
    }

    test("does not find win in row when players mixed") {
        val board = Board()
        board.play(Player.COMPUTER, Coordinate(0, 0)) shouldBe false
        board.play(Player.USER, Coordinate(1, 0)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(2, 0)) shouldBe false
        board.winner shouldBe Player.NONE
    }

    test("does not find win in column when players mixed") {
        val board = Board()
        board.play(Player.USER, Coordinate(0, 1)) shouldBe false
        board.play(Player.USER, Coordinate(1, 1)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(2, 1)) shouldBe false
        board.winner shouldBe Player.NONE
    }

    test("does not find win in diagonal from top when players mixed") {
        val board = Board()
        board.play(Player.USER, Coordinate(0, 0)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(2, 2)) shouldBe false
        board.play(Player.USER, Coordinate(1, 1)) shouldBe false
        board.winner shouldBe Player.NONE
    }

    test("does not find win in diagonal from bottom when players mixed") {
        val board = Board()
        board.play(Player.COMPUTER, Coordinate(0, 2)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(1, 1)) shouldBe false
        board.play(Player.USER, Coordinate(2, 0)) shouldBe false
        board.winner shouldBe Player.NONE
    }

    test("finds draw") {
        val board = Board()
        board.play(Player.USER, Coordinate(0, 0)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(0, 1)) shouldBe false
        board.play(Player.USER, Coordinate(1, 2)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(2, 2)) shouldBe false
        board.play(Player.USER, Coordinate(2, 1)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(1, 1)) shouldBe false
        board.play(Player.USER, Coordinate(0, 2)) shouldBe false
        board.play(Player.COMPUTER, Coordinate(2, 0)) shouldBe false
        board.play(Player.USER, Coordinate(1, 0)) shouldBe false
        board.hasUnplacedSpaces() shouldBe false
    }

    test("throws exception on attempt to replay space") {
        val board = Board()
        board.play(Player.COMPUTER, Coordinate(0, 1))
        shouldThrow<UnplayableSpaceException> {
            board.play(Player.USER, Coordinate(0, 1))
        }
    }

    test("finds unplayed spaces") {
        val board = Board()
        var unplayedSpacesById: Sequence<Int>
        board.getUnplayedSpaces() shouldHaveSize 9
        board.play(Player.COMPUTER, 1)
        unplayedSpacesById = board.getUnplayedSpaces().map { it.id }
        unplayedSpacesById shouldHaveSize 8
        unplayedSpacesById shouldNotContain  1
        board.play(Player.USER, 8)
        unplayedSpacesById = board.getUnplayedSpaces().map { it.id }
        unplayedSpacesById shouldHaveSize 7
        unplayedSpacesById shouldNotContain 1
        unplayedSpacesById shouldNotContain 8
    }

    test("spaces playable by ID") {
        val board = Board()
        board.play(Player.COMPUTER, 2)
        shouldThrow<UnplayableSpaceException> {
            board.play(Player.USER, Coordinate(0, 1))
        }
    }

    test("canPlay returns true for unplayed space") {
        val board = Board()
        board.play(Player.USER, 4)
        board.canPlay(Coordinate(0, 0)) shouldBe true
    }

    test("canPlay returns false for played space") {
        val board = Board()
        board.play(Player.USER, 4)
        board.canPlay(Coordinate(1, 0)) shouldBe false
    }

    test("all win possibilities found on empty board") {
        Board().getWaysToWin(Player.USER) shouldHaveSize 8
    }

    test("win possibilities prioritized when board has one play") {
        val board = Board()
        board.play(Player.USER, 1)
        val waysToWinById = board.getWaysToWin(Player.USER).map {
            it.map { it.id }
        }
        waysToWinById shouldHaveSize 8
        waysToWinById.get(0) shouldBe listOf(2, 3)
        waysToWinById.get(1) shouldBe listOf(4, 7)
    }

    test("appropriate win possibilities excluded when board has plays from both players") {
        val board = Board()
        board.play(Player.USER, 1)
        board.play(Player.COMPUTER, 3)
        var waysToWinById = board.getWaysToWin(Player.USER).map {
            it.map { it.id }
        }
        waysToWinById shouldHaveSize 5
        waysToWinById shouldNotContain listOf(2, 3)
        waysToWinById.get(0) shouldBe listOf(4, 7)
        waysToWinById = board.getWaysToWin(Player.COMPUTER).map {
            it.map { it.id }
        }
        waysToWinById shouldHaveSize 5
        waysToWinById shouldNotContain listOf(1, 2)
        waysToWinById.get(0) shouldBe listOf(6, 9)
    }
})
