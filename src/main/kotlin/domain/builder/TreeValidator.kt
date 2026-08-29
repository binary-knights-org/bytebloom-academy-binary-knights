package domain.builder

import domain.model.tree.HubNode

class TreeValidator {

    fun hasNoCycles(root: HubNode.GlobalHub): Boolean {
        val visitedIds = mutableSetOf<String>()
        return visitNode(root, visitedIds)
    }

    private fun visitNode(node: HubNode, visitedIds: MutableSet<String>): Boolean {
        if (!visitedIds.add(node.warehouse.id)) return false
        return childrenOf(node).all { visitNode(it, visitedIds) }
    }

    fun coversAllWarehouses(root: HubNode.GlobalHub, expectedCount: Int): Boolean {
        return countNodes(root) == expectedCount
    }

    private fun countNodes(node: HubNode): Int {
        return 1 + childrenOf(node).sumOf { countNodes(it) }
    }

    fun verifyAllLocalDepotsReachRoot(root: HubNode.GlobalHub): Boolean {
        val allDepots = collectLocalDepots(root)
        return allDepots.all { depot -> climbToRoot(depot) === root }
    }

    private fun collectLocalDepots(node: HubNode): List<HubNode.LocalDepot> = when (node) {
        is HubNode.GlobalHub -> node.children.flatMap { collectLocalDepots(it) }
        is HubNode.RegionalCenter -> node.children.flatMap { collectLocalDepots(it) }
        is HubNode.LocalDepot -> listOf(node)
    }

    private tailrec fun climbToRoot(node: HubNode): HubNode.GlobalHub = when (node) {
        is HubNode.GlobalHub -> node
        is HubNode.RegionalCenter -> climbToRoot(node.parent)
        is HubNode.LocalDepot -> climbToRoot(node.parent)
    }

    private fun childrenOf(node: HubNode): List<HubNode> = when (node) {
        is HubNode.GlobalHub -> node.children
        is HubNode.RegionalCenter -> node.children
        is HubNode.LocalDepot -> emptyList()
    }
}