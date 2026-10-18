package domain.command

class CommandInvoker {

    private val undoStack = ArrayDeque<Command>()
    private val redoStack = ArrayDeque<Command>()

    val undoHistorySize: Int
        get() = undoStack.size

    val redoHistorySize: Int
        get() = redoStack.size

    suspend fun executeCommand(command: Command): Boolean {
        val success = command.execute()

        if (success) {
            undoStack.addLast(command)
            redoStack.clear()
        }

        return success
    }

    suspend fun undo(): Boolean {
        if (undoStack.isEmpty()) return false

        val lastCommand = undoStack.removeLast()
        val success = lastCommand.undo()

        if (success) {
            redoStack.addLast(lastCommand)
        } else {
            undoStack.addLast(lastCommand)
        }

        return success
    }

    suspend fun redo(): Boolean {
        if (redoStack.isEmpty()) return false

        val lastCommand = redoStack.removeLast()
        val success = lastCommand.execute()

        if (success) {
            undoStack.addLast(lastCommand)
        } else {
            redoStack.addLast(lastCommand)
        }

        return success
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()

    fun canRedo(): Boolean = redoStack.isNotEmpty()

    fun getUndoHistoryDescriptions(): List<String> =
        undoStack.map { it.description }

    fun getRedoHistoryDescriptions(): List<String> =
        redoStack.map { it.description }
}
