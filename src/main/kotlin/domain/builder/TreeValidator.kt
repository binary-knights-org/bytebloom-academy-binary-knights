package domain.builder

import domain.algorithm.tree.HubNode

class TreeValidator {

    fun hasNoCycles(root: HubNode.GlobalHub): Boolean =
        visitNode(root, mutableSetOf())

    private fun visitNode(node: HubNode, visitedIds: MutableSet<String>): Boolean {
        if (!visitedIds.add(node.warehouse.id)) return false

        return when (node) {
            is HubNode.GlobalHub -> node.children.all { visitNode(it, visitedIds) }
            is HubNode.RegionalCenter -> node.children.all { visitNode(it, visitedIds) }
            is HubNode.LocalDepot -> true
        }
    }

    fun coversAllWarehouses(root: HubNode.GlobalHub, expectedCount: Int): Boolean =
        countNodes(root) == expectedCount

    private fun countNodes(node: HubNode): Int = 1 + when (node) {
        is HubNode.GlobalHub -> node.children.sumOf { countNodes(it) }
        is HubNode.RegionalCenter -> node.children.sumOf { countNodes(it) }
        is HubNode.LocalDepot -> 0
    }

    fun verifyAllLocalDepotsReachRoot(root: HubNode.GlobalHub): Boolean =
        collectLocalDepots(root).all { depot -> depot.parent.parent === root }

    private fun collectLocalDepots(root: HubNode.GlobalHub): List<HubNode.LocalDepot> =
        root.children.flatMap { it.children }
}
