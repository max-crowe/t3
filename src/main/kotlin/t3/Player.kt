package t3

enum class Player {
    USER {
        override fun toString() = "X"
    },
    COMPUTER {
        override fun toString() = "O"
    },
    NONE {
        override fun toString() = " "
    }
}