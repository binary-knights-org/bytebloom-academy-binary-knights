package domain.algorithm.tree

import domain.model.Warehouse

sealed interface HubNode {
    val warehouse: Warehouse

    class GlobalHub(
        override val warehouse: Warehouse,
        val children: List<RegionalCenter> = emptyList()
    ) : HubNode

    class RegionalCenter(
        override val warehouse: Warehouse,
        val parent: GlobalHub,
        val children: List<LocalDepot> = emptyList()
    ) : HubNode

    class LocalDepot(
        override val warehouse: Warehouse,
        val parent: RegionalCenter
    ) : HubNode
}
