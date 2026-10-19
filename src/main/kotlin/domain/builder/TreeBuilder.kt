package domain.builder

import domain.algorithm.tree.HubNode
import domain.model.Warehouse
import domain.model.exception.ResourceNotFoundException

class TreeBuilder {

    fun buildTree(warehouses: List<Warehouse>): HubNode.GlobalHub {
        if (warehouses.isEmpty()) {
            throw ResourceNotFoundException(    )
        }

        val globalHubWarehouse = warehouses.first()
        val remainingWarehouses = warehouses.filter { it.id != globalHubWarehouse.id }
        val regionalGroups = remainingWarehouses.groupBy { it.regionalZone }

        lateinit var globalHub: HubNode.GlobalHub

        val regionalCenters = regionalGroups.values.map { zoneWarehouses ->
            lateinit var regionalCenter: HubNode.RegionalCenter

            val localDepots = zoneWarehouses.drop(1).map { depotWarehouse ->
                HubNode.LocalDepot(depotWarehouse, parent = regionalCenter)
            }

            regionalCenter = HubNode.RegionalCenter(
                warehouse = zoneWarehouses.first(),
                parent = globalHub,
                children = localDepots
            )
            regionalCenter
        }

        globalHub = HubNode.GlobalHub(globalHubWarehouse, regionalCenters)
        return globalHub
    }
}
