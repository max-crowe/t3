package t3.io

import t3.Board

class StdoutHandler : OutputHandler {
    override fun write(output: String) {
        print(output)
    }

    override fun writeln(output: String) {
        println(output)
    }
}