package domain.builder

import domain.model.Warehouse
import domain.model.tree.HubNode

private const val REGIONAL_CENTER_COUNT_PER_ZONE = 1

class TreeBuilder {

    fun buildTree(warehouses: List<Warehouse>): HubNode.GlobalHub {
        val globalHubWarehouse = warehouses.random()
        val globalHub = HubNode.GlobalHub(globalHubWarehouse)

        val remainingWarehouses = warehouses.filter { it.id != globalHubWarehouse.id }
        val regionalGroups = remainingWarehouses.groupBy { it.regionalZone }

        globalHub.children = regionalGroups.values.map { warehousesInZone ->
            buildRegionalCenter(warehousesInZone, globalHub)
        }

        return globalHub
    }

    private fun buildRegionalCenter(
        warehousesInZone: List<Warehouse>,
        parent: HubNode.GlobalHub
    ): HubNode.RegionalCenter {
        val regionalCenter = HubNode.RegionalCenter(warehousesInZone.first(), parent)

        regionalCenter.children = warehousesInZone.drop(REGIONAL_CENTER_COUNT_PER_ZONE).map { depotWarehouse ->
            HubNode.LocalDepot(depotWarehouse, parent = regionalCenter)
        }

        return regionalCenter
    }
}
