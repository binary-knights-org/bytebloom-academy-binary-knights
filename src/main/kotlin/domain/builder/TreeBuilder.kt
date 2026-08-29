package domain.builder

import domain.model.Warehouse
import domain.model.tree.HubNode

class TreeBuilder {

    fun buildTree(warehouses: List<Warehouse>): HubNode.GlobalHub {
        val globalHubWarehouse = warehouses.random()
        val globalHub = HubNode.GlobalHub(globalHubWarehouse)

        val remainingWarehouses = warehouses.filter { it.id != globalHubWarehouse.id }
        val regionalGroups = remainingWarehouses.groupBy { it.regionalZone }

        regionalGroups.values.forEach { warehousesInZone ->
            attachZoneToGlobalHub(warehousesInZone, globalHub)
        }

        return globalHub
    }

    private fun attachZoneToGlobalHub(
        warehousesInZone: List<Warehouse>,
        globalHub: HubNode.GlobalHub
    ) {
        if (warehousesInZone.isEmpty()) return

        val regionalCenter = HubNode.RegionalCenter(warehousesInZone.first(), parent = globalHub)
        globalHub.children.add(regionalCenter)

        warehousesInZone.drop(1).forEach { depotWarehouse ->
            val depot = HubNode.LocalDepot(depotWarehouse, parent = regionalCenter)
            regionalCenter.children.add(depot)
        }
    }
}
