package domain.builder

import dataholder.FleetRaw
import dataholder.PackageRaw
import dataholder.RouteRaw
import dataholder.WarehouseRaw
import domain.model.Warehouse

class DomainGraphBuilder {

    fun buildGraph(
        rawVehicles: List<FleetRaw>,
        rawPackages: List<PackageRaw>,
        rawRoutes: List<RouteRaw>,
        rawWarehouses: List<WarehouseRaw>
    ): List<Warehouse> {

        val warehouseRawMap = rawWarehouses.associateBy { it.hubId }
        val vehiclesGroup = rawVehicles.groupBy { it.currentHubId }
        val packagesGroup = rawPackages.groupBy { it.originHubId }
        val routesGroup = rawRoutes.groupBy { it.originHubId }
        return emptyList()
    }
}
