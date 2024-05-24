package t3.io

class StdoutHandler : OutputHandler {
    override fun write(output: String) {
        print(output)
    }

    override fun writeln(output: String) {
        println(output)
    }
}