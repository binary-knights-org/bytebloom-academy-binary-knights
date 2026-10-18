package domain.algorithm.tree

internal data class TreeNode(
    val id: String,
    val left: TreeNode? = null,
    val right: TreeNode? = null
) {
    fun searchWithStepCount(targetId: String, steps: Int = 0): Pair<Boolean, Int> {
        val currentSteps = steps + 1
        return when {
            targetId == id -> Pair(true, currentSteps)
            targetId < id -> left?.searchWithStepCount(targetId, currentSteps) ?: Pair(false, currentSteps)
            else -> right?.searchWithStepCount(targetId, currentSteps) ?: Pair(false, currentSteps)
        }
    }
}
