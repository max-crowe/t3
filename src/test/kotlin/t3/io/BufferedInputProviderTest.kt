package t3.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BufferedInputProviderTest : FunSpec({
    test("returns null when buffer empty") {
        val provider = BufferedInputProvider()
        provider.get() shouldBe null
    }

    test("returns buffer contents") {
        val provider = BufferedInputProvider()
        var content = """
            foo bar
            baz
        """.trimIndent()
        provider.set(content)
        provider.get() shouldBe content
        provider.get() shouldBe null
        content = "abc123"
        provider.set(content)
        provider.get() shouldBe content
    }
})