package domain.builder

import domain.model.tree.HubNode

class TreeValidator {

    fun hasNoCycles(root: HubNode.GlobalHub): Boolean {
        val visitedIds = mutableSetOf<String>()
        return visitNode(root, visitedIds)
    }

    private fun visitNode(node: HubNode, visitedIds: MutableSet<String>): Boolean {
        if (!visitedIds.add(node.warehouse.id)) return false

        return when (node) {
            is HubNode.GlobalHub -> node.children.all { visitNode(it, visitedIds) }
            is HubNode.RegionalCenter -> node.children.all { visitNode(it, visitedIds) }
            is HubNode.LocalDepot -> true
        }
    }

    fun coversAllWarehouses(root: HubNode.GlobalHub, expectedCount: Int): Boolean {
        return countNodes(root) == expectedCount
    }

    private fun countNodes(node: HubNode): Int {
        return 1 + when (node) {
            is HubNode.GlobalHub -> node.children.sumOf { countNodes(it) }
            is HubNode.RegionalCenter -> node.children.sumOf { countNodes(it) }
            is HubNode.LocalDepot -> 0
        }
    }

    fun verifyAllLocalDepotsReachRoot(root: HubNode.GlobalHub): Boolean {
        val allDepots = collectLocalDepots(root)
        return allDepots.all { depot -> depot.parent.parent === root }
    }

    private fun collectLocalDepots(node: HubNode.GlobalHub): List<HubNode.LocalDepot> {
        return node.children.flatMap { regionalCenter -> regionalCenter.children }
    }
}
