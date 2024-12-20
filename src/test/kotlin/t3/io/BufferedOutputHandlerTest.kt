package t3.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BufferedOutputHandlerTest : FunSpec({
    test("read from handler with empty buffer returns null") {
        val handler = BufferedOutputHandler()
        handler.read() shouldBe null
    }

    test("can write to and read from handler") {
        val handler = BufferedOutputHandler()
        var contents = """
            foo bar
            baz
        """.trimIndent()
        handler.write(contents)
        handler.read() shouldBe contents
        contents = "abc123"
        handler.write(contents)
        handler.read() shouldBe contents
    }

    test("writeln appends newline") {
        val handler = BufferedOutputHandler()
        val contents = "foo bar"
        handler.writeln(contents)
        handler.read() shouldBe "${contents}\n"
    }
})