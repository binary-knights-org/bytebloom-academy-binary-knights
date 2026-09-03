package domain.command

import java.util.Stack

class CommandInvoker {

    private val undoStack = Stack<Command>()
    private val redoStack = Stack<Command>()

    val undoHistorySize: Int
        get() = undoStack.size

    val redoHistorySize: Int
        get() = redoStack.size

    fun executeCommand(command: Command): Boolean {
        val success = command.execute()

        if (success) {
            undoStack.push(command)
            redoStack.clear()
        }

        return success
    }

    fun undo(): Boolean {
        if (undoStack.isEmpty()) return false

        val lastCommand = undoStack.pop()
        val success = lastCommand.undo()

        if (success) {
            redoStack.push(lastCommand)
        } else {
            undoStack.push(lastCommand)
        }

        return success
    }

    fun redo(): Boolean {
        if (redoStack.isEmpty()) return false

        val lastCommand = redoStack.pop()
        val success = lastCommand.execute()

        if (success) {
            undoStack.push(lastCommand)
        } else {
            redoStack.push(lastCommand)
        }

        return success
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()

    fun canRedo(): Boolean = redoStack.isNotEmpty()

    fun getUndoHistoryDescriptions(): List<String> {
        return undoStack.map { it.description }
    }

    fun getRedoHistoryDescriptions(): List<String> {
        return redoStack.map { it.description }
    }
}
