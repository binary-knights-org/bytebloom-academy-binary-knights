package domain.model.tree

import domain.model.Warehouse

sealed interface HubNode {
    val warehouse: Warehouse

    class GlobalHub(
        override val warehouse: Warehouse
    ) : HubNode {
        lateinit var children: List<RegionalCenter>
            internal set
    }

    class RegionalCenter(
        override val warehouse: Warehouse,
        val parent: GlobalHub
    ) : HubNode {
        lateinit var children: List<LocalDepot>
            internal set
    }

    class LocalDepot(
        override val warehouse: Warehouse,
        val parent: RegionalCenter
    ) : HubNode
}

