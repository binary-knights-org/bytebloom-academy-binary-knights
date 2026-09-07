package domain.tree

class BalancedTree private constructor(
    private val root: TreeNode
) : TreeSearchTarget {
    constructor() : this(TreeNode.Empty)

    override fun insert(id: String): BalancedTree {
        val ids = collectIds(root) + id
        val sortedIds = ids.sorted()
        return BalancedTree(buildBalancedTree(sortedIds))
    }

    override fun searchWithStepCount(targetId: String): Pair<Boolean, Int> {
        return searchNode(root, targetId, INITIAL_STEP_COUNT)
    }

    private fun buildBalancedTree(ids: List<String>): TreeNode {
        if (ids.isEmpty()) {
            return TreeNode.Empty
        }
        val middleIndex = ids.size / HALF_DIVISOR
        val middleId = ids[middleIndex]

        return TreeNode.Node(
            id = middleId,
            left = buildBalancedTree(ids.subList(0, middleIndex)),
            right = buildBalancedTree(ids.subList(middleIndex + 1, ids.size))
        )
    }

    private fun collectIds(node: TreeNode): List<String> {
        return when (node) {
            TreeNode.Empty -> emptyList()
            is TreeNode.Node -> collectIds(node.left) + node.id + collectIds(node.right)
        }
    }

    private fun searchNode(
        node: TreeNode,
        targetId: String,
        steps: Int
    ): Pair<Boolean, Int> {
        return when (node) {
            TreeNode.Empty -> Pair(false, steps)
            is TreeNode.Node -> {
                val currentSteps = steps + STEP_INCREMENT
                when {
                    targetId == node.id -> Pair(true, currentSteps)
                    targetId < node.id -> searchNode(node.left, targetId, currentSteps)
                    else -> searchNode(node.right, targetId, currentSteps)
                }
            }
        }
    }

    private sealed interface TreeNode {
        data object Empty : TreeNode
        data class Node(
            val id: String,
            val left: TreeNode = Empty,
            val right: TreeNode = Empty
        ) : TreeNode
    }

    private companion object {
        const val HALF_DIVISOR = 2
        const val INITIAL_STEP_COUNT = 0
        const val STEP_INCREMENT = 1
    }
}
