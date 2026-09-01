package domain.command

import java.util.ArrayDeque

class CommandInvoker {
    private val history = ArrayDeque<Command>()

    val historySize: Int
        get() = history.size

    fun executeCommand(command: Command): Boolean {
        val success = command.execute()
        if (success) {
            history.push(command)
        }
        return success
    }

    fun undo(): Boolean {
        if (history.isEmpty()) return false
        val lastCommand = history.pop()
        return lastCommand.undo()
    }

    fun canUndo(): Boolean = history.isNotEmpty()

    fun getHistoryDescriptions(): List<String> {
        return history.map { it.description }
    }
}
