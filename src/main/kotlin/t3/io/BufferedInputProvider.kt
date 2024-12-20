package t3.io

import kotlinx.io.Buffer
import kotlinx.io.readString
import kotlinx.io.writeString

class BufferedInputProvider : InputProvider {
    private val buffer = Buffer()

    override fun get(): String? {
        val contents = buffer.readString()
        if (contents == "") {
            return null
        }
        return contents
    }

    fun set(contents: String) {
        buffer.writeString(contents)
    }
}