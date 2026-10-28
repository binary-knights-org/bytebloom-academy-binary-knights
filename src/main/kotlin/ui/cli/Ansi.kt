package ui.cli

internal object Ansi {
    const val RESET = "\u001B[0m"
    const val RED = "\u001B[31m"
    const val YELLOW = "\u001B[33m"
    const val BLUE = "\u001B[34m"
    const val CYAN = "\u001B[36m"
    const val GRAY = "\u001B[90m"
    const val REVERSE = "\u001B[7m"
    const val CLEAR_SCREEN = "\u001B[2J\u001B[H"
}
