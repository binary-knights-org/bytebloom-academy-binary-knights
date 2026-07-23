package utils


private const val PRIORITY_URGENT_TEXT = "URGENT"
private const val PRIORITY_STANDARD_TEXT = "STANDARD"
private const val PRIORITY_LOW_TEXT = "LOW"

private const val RANK_URGENT = 3
private const val RANK_STANDARD = 2
private const val RANK_LOW = 1
private const val RANK_DEFAULT = 1

fun getPriorityRank(priority: String): Int {
    return when (priority.uppercase()) {
        PRIORITY_URGENT_TEXT -> RANK_URGENT
        PRIORITY_STANDARD_TEXT -> RANK_STANDARD
        PRIORITY_LOW_TEXT -> RANK_LOW
        else -> RANK_DEFAULT
    }
}