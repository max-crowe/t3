package t3.io

class StdinProvider : InputProvider {
    override fun get() = readlnOrNull()
}