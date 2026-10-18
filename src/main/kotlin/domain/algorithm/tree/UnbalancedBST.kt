package domain.algorithm.tree

class UnbalancedBST private constructor(
    private val root: TreeNode?
) : TreeSearchTarget {
    constructor() : this(null)

    override fun insert(id: String): UnbalancedBST =
        UnbalancedBST(insertNode(root, id))

    override fun searchWithStepCount(targetId: String): Pair<Boolean, Int> =
        root?.searchWithStepCount(targetId) ?: Pair(false, 0)

    private fun insertNode(node: TreeNode?, id: String): TreeNode = if (node == null) {
        TreeNode(id)
    } else {
        when {
            id < node.id -> node.copy(left = insertNode(node.left, id))
            id > node.id -> node.copy(right = insertNode(node.right, id))
            else -> node
        }
    }
}
