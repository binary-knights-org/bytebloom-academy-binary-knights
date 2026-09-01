package domain.command

interface Command {
    val description: String
    fun execute(): Boolean
    fun undo(): Boolean
}
