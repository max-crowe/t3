package t3.io

import kotlinx.io.Buffer
import kotlinx.io.readString
import kotlinx.io.writeString

class BufferedOutputHandler : OutputHandler {
    private val buffer = Buffer()

    override fun write(output: String) {
        buffer.writeString(output)
    }

    override fun writeln(output: String) {
        write("${output}\n")
    }

    fun read(): String? {
        val contents = buffer.readString()
        if (contents == "") {
            return null
        }
        return contents
    }
}