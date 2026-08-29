package domain.model.tree

import domain.model.Warehouse

sealed interface HubNode {
    val warehouse: Warehouse

    class GlobalHub(
        override val warehouse: Warehouse,
        val children: MutableList<HubNode> = mutableListOf()
    ) : HubNode

    class RegionalCenter(
        override val warehouse: Warehouse,
        val parent: HubNode,
        val children: MutableList<HubNode> = mutableListOf()
    ) : HubNode

    class LocalDepot(
        override val warehouse: Warehouse,
        val parent: HubNode
    ) : HubNode
}