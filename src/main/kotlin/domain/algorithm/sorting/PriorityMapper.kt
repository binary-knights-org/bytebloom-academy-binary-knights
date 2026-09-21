package domain.algorithm.sorting

import domain.model.Priority

private const val RANK_URGENT = 3
private const val RANK_STANDARD = 2
private const val RANK_LOW = 1

fun getPriorityRank(priority: Priority): Int {
    return when (priority) {
        Priority.URGENT -> RANK_URGENT
        Priority.STANDARD -> RANK_STANDARD
        Priority.LOW -> RANK_LOW
    }
}
