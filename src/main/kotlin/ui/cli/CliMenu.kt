package ui.cli

internal object CliMenu {

    fun printMainMenu() {
        println(
            "\u001B[34m" +
                "1. Calculate Route\n" +
                "2. Classify Package via KNN\n" +
                "3. Real-time Telemetry Streaming Logs (10 notifications)\n" +
                "0. Exit"
        )

        print("\u001B[35mChoose an option: ")
    }

    fun printInvalidOption() {
        println(
            "\u001B[31m" +
                "Invalid option. Please choose 0, 1, 2, or 3."
        )
    }

    fun printSeparator() {
        println("\u001B[90m-----------------------------------------------")
    }

    fun printExitMessage() {
        println("\u001B[95mExiting Logistics CLI...")
    }

    fun printGoodbye() {
        println("\u001B[95mGoodbye!")
    }
}
