package domain.algorithm.tree

class BalancedTree private constructor(
    private val root: TreeNode?
) : TreeSearchTarget {
    constructor() : this(null)

    override fun insert(id: String): BalancedTree {
        val existingIds = collectIds(root)
        if (id in existingIds) return this

        val sortedIds = (existingIds + id).sorted()
        return BalancedTree(buildBalancedTree(sortedIds))
    }

    override fun searchWithStepCount(targetId: String): Pair<Boolean, Int> =
        root?.searchWithStepCount(targetId) ?: Pair(false, 0)

    private fun buildBalancedTree(ids: List<String>): TreeNode? {
        if (ids.isEmpty()) return null

        val middleIndex = ids.size / 2
        return TreeNode(
            id = ids[middleIndex],
            left = buildBalancedTree(ids.subList(0, middleIndex)),
            right = buildBalancedTree(ids.subList(middleIndex + 1, ids.size))
        )
    }

    private fun collectIds(node: TreeNode?): List<String> = if (node == null) {
        emptyList()
    } else {
        collectIds(node.left) + node.id + collectIds(node.right)
    }
}
