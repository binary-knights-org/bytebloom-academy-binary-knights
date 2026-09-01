package domain.tree

class UnbalancedBST private constructor(
    private val root: TreeNode
) : TreeSearchTarget {
    constructor() : this(TreeNode.Empty)

    override fun insert(id: String): UnbalancedBST {
        return UnbalancedBST(insertNode(root, id))
    }

    override fun searchWithStepCount(targetId: String): Pair<Boolean, Int> {
        return searchNode(root, targetId, INITIAL_STEP_COUNT)
    }

    private fun insertNode(node: TreeNode, id: String): TreeNode {
        return when (node) {
            TreeNode.Empty -> TreeNode.Node(id)
            is TreeNode.Node -> when {
                id < node.id -> node.copy(left = insertNode(node.left, id))
                id > node.id -> node.copy(right = insertNode(node.right, id))
                else -> node
            }
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
        const val INITIAL_STEP_COUNT = 0
        const val STEP_INCREMENT = 1
    }
}

