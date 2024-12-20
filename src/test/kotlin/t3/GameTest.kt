package t3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import t3.io.StdoutHandler
import t3.io.StdinProvider
import t3.strategy.AlgorithmicStrategyProvider


class GameTest : FunSpec({
    test("human wins") {
        val inputProvider = mockk<StdinProvider>()
        val strategyProvider = mockk<AlgorithmicStrategyProvider>()
        val capturedOutput = mutableListOf<String>()
        val outputHandler = mockk<StdoutHandler>()

        every { inputProvider.get() } returnsMany listOf("1", "2", "3", "n")
        every { strategyProvider.getSpace(any()) } returnsMany listOf(4, 5)
        every { outputHandler.writeln(any(String::class)) } just Runs
        every { outputHandler.writeln(any(Board::class)) } just Runs
        every { outputHandler.write(capture(capturedOutput)) } just Runs

        val game = Game(
            inputProvider = inputProvider,
            outputHandler = outputHandler,
            strategyProvider = strategyProvider
        )
        game.run()

        capturedOutput.get(capturedOutput.size - 2) shouldContain "You did it!"
    }

    test("computer wins") {
        val inputProvider = mockk<StdinProvider>()
        val strategyProvider = mockk<AlgorithmicStrategyProvider>()
        val capturedOutput = mutableListOf<String>()
        val outputHandler = mockk<StdoutHandler>()

        every { inputProvider.get() } returnsMany listOf("1", "2", "4", "n")
        every { strategyProvider.getSpace(any()) } returnsMany listOf(3, 6, 9)
        every { outputHandler.writeln(any(String::class)) } just Runs
        every { outputHandler.writeln(any(Board::class)) } just Runs
        every { outputHandler.write(capture(capturedOutput)) } just Runs

        val game = Game(
            inputProvider = inputProvider,
            outputHandler = outputHandler,
            strategyProvider = strategyProvider
        )
        game.run()

        capturedOutput.get(capturedOutput.size - 2) shouldContain "computer wins"
    }

    test("human wins, computer wins on replay") {
        val inputProvider = mockk<StdinProvider>()
        val strategyProvider = mockk<AlgorithmicStrategyProvider>()
        val capturedOutput = mutableListOf<String>()
        val outputHandler = mockk<StdoutHandler>()

        every { inputProvider.get() } returnsMany listOf("1", "2", "3", "y", "1", "2", "4", "n")
        every { strategyProvider.getSpace(any()) } returnsMany listOf(4, 5, 3, 6, 9)
        every { outputHandler.writeln(any(String::class)) } just Runs
        every { outputHandler.writeln(any(Board::class)) } just Runs
        every { outputHandler.write(capture(capturedOutput)) } just Runs

        val game = Game(
            inputProvider = inputProvider,
            outputHandler = outputHandler,
            strategyProvider = strategyProvider
        )
        game.run()

        val output = capturedOutput.joinToString("\n")
        output.indexOf("You did it!") shouldBeLessThan output.indexOf("computer wins")
    }

    test("human makes invalid space choices") {
        val inputProvider = mockk<StdinProvider>()
        val strategyProvider = mockk<AlgorithmicStrategyProvider>()
        val capturedOutput = mutableListOf<String>()
        val outputHandler = mockk<StdoutHandler>()

        every { inputProvider.get() } returnsMany listOf("1", "2", "4", "x", "3", "n")
        every { strategyProvider.getSpace(any()) } returnsMany listOf(4, 5)
        every { outputHandler.writeln(any(String::class)) } just Runs
        every { outputHandler.writeln(any(Board::class)) } just Runs
        every { outputHandler.write(capture(capturedOutput)) } just Runs

        val game = Game(
            inputProvider = inputProvider,
            outputHandler = outputHandler,
            strategyProvider = strategyProvider
        )
        game.run()

        val output = capturedOutput.joinToString("\n")
        output shouldContain "Space 4 has already been played"
        output shouldContain "x isn't a playable space"
    }

    test("human wins, then makes invalid replay choice") {
        val inputProvider = mockk<StdinProvider>()
        val strategyProvider = mockk<AlgorithmicStrategyProvider>()
        val capturedOutput = mutableListOf<String>()
        val outputHandler = mockk<StdoutHandler>()

        every { inputProvider.get() } returnsMany listOf("1", "2", "3", "q", "n")
        every { strategyProvider.getSpace(any()) } returnsMany listOf(4, 5)
        every { outputHandler.writeln(any(String::class)) } just Runs
        every { outputHandler.writeln(any(Board::class)) } just Runs
        every { outputHandler.write(capture(capturedOutput)) } just Runs

        val game = Game(
            inputProvider = inputProvider,
            outputHandler = outputHandler,
            strategyProvider = strategyProvider
        )
        game.run()

        val output = capturedOutput.joinToString("\n")
        output shouldContain "Invalid input; try again"
    }

    test("draw, then human wins on replay") {
        val inputProvider = mockk<StdinProvider>()
        val strategyProvider = mockk<AlgorithmicStrategyProvider>()
        val capturedOutput = mutableListOf<String>()
        val outputHandler = mockk<StdoutHandler>()

        every { inputProvider.get() } returnsMany listOf("1", "4", "5", "8", "3", "y", "1", "2", "3", "n")
        every { strategyProvider.getSpace(any()) } returnsMany listOf(2, 7, 9, 6, 4, 5)
        every { outputHandler.writeln(any(String::class)) } just Runs
        every { outputHandler.writeln(any(Board::class)) } just Runs
        every { outputHandler.write(capture(capturedOutput)) } just Runs

        val game = Game(
            inputProvider = inputProvider,
            outputHandler = outputHandler,
            strategyProvider = strategyProvider
        )
        game.run()

        val output = capturedOutput.joinToString("\n")
        output.indexOf("It's a draw!") shouldBeLessThan output.indexOf("You did it!")
    }

    test("can serialize and deserialize") {
        val inputProvider = mockk<StdinProvider>()
        val strategyProvider = mockk<AlgorithmicStrategyProvider>()
        val capturedOutput = mutableListOf<String>()
        val outputHandler = mockk<StdoutHandler>()

        every { inputProvider.get() } returnsMany listOf("1", "2", "3", "n")
        every { strategyProvider.getSpace(any()) } returnsMany listOf(4, 5)
        every { outputHandler.writeln(any(String::class)) } just Runs
        every { outputHandler.writeln(any(Board::class)) } just Runs
        every { outputHandler.write(capture(capturedOutput)) } just Runs

        val game1 = Game(
            inputProvider = inputProvider,
            outputHandler = outputHandler,
            strategyProvider = strategyProvider
        )
        game1.playRound() shouldBe null
        game1.playRound() shouldBe null

        val game2 = Game.fromJson(game1.toJson(), inputProvider, outputHandler, strategyProvider)
        game2.run()

        capturedOutput.get(capturedOutput.size - 2) shouldContain "You did it!"
    }

    test("expected messages are skipped in socket context") {
        val inputProvider = mockk<StdinProvider>()
        val strategyProvider = mockk<AlgorithmicStrategyProvider>()
        val capturedOutput = mutableListOf<String>()
        val outputHandler = mockk<StdoutHandler>()

        every { inputProvider.get() } returnsMany listOf("1", "2", "3", "n")
        every { strategyProvider.getSpace(any()) } returnsMany listOf(4, 5)
        every { outputHandler.writeln(any(String::class)) } just Runs
        every { outputHandler.writeln(any(Board::class)) } just Runs
        every { outputHandler.write(capture(capturedOutput)) } just Runs

        val game = Game(
            context = Context.SOCKET,
            inputProvider = inputProvider,
            outputHandler = outputHandler,
            strategyProvider = strategyProvider
        )
        game.run()

        val output = capturedOutput.joinToString("\n")
        output shouldNotContain Game.INTRO_MESSAGE_LINE_2
        output shouldNotContain Board().getEmptyLayout()
        verify(exactly = 0) {
            outputHandler.writeln(any(Board::class))
        }
    }
})
