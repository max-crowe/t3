package t3.io

import t3.Board

interface OutputHandler {
    fun write(output: String)
    fun writeln(output: String)

    fun writeln(output: Board) {
        writeln("${output}\n")
    }
}