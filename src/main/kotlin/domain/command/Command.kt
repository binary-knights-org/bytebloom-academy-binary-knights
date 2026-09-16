package domain.command

interface Command {
    val description: String
    suspend  fun execute(): Boolean
    suspend  fun undo(): Boolean

}
