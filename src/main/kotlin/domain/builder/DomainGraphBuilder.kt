package domain.builder

import dataholder.FleetRaw
import dataholder.PackageRaw
import dataholder.RouteRaw
import dataholder.WarehouseRaw
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse

class DomainGraphBuilder {

    fun buildGraph(
        rawVehicles: List<FleetRaw>,
        rawPackages: List<PackageRaw>,
        rawRoutes: List<RouteRaw>,
        rawWarehouses: List<WarehouseRaw>
    ): List<Warehouse> {

        val vehiclesByHubId = rawVehicles.groupBy { it.currentHubId }
        val packagesByOriginId = rawPackages.groupBy { it.originHubId }
        val routesByOriginId = rawRoutes.groupBy { it.originHubId }

        val warehousesMap = createWarehouseNodes(rawWarehouses)

        attachVehiclesToWarehouses(warehousesMap, vehiclesByHubId)
        attachPackagesToWarehouses(warehousesMap, packagesByOriginId)
        attachRoutesToWarehouses(warehousesMap, routesByOriginId)

        return warehousesMap.values.toList()
    }

    private fun createWarehouseNodes(rawWarehouses: List<WarehouseRaw>): Map<String, Warehouse> {
        return rawWarehouses.associate { rawHub ->
            rawHub.hubId to createWarehouse(rawHub)
        }
    }

    private fun createWarehouse(rawHub: WarehouseRaw): Warehouse {
        return Warehouse(
            id = rawHub.hubId,
            name = rawHub.hubName,
            regionalZone = rawHub.regionalZone
        )
    }

    private fun attachVehiclesToWarehouses(
        warehousesMap: Map<String, Warehouse>,
        vehiclesByHubId: Map<String, List<FleetRaw>>
    ) {
        for ((hubId, warehouse) in warehousesMap) {
            val rawFleet = vehiclesByHubId[hubId] ?: continue
            for (vRaw in rawFleet) {
                val vehicle = createVehicle(vRaw, warehouse)
                warehouse.addVehicle(vehicle)
            }
        }
    }

    private fun createVehicle(vRaw: FleetRaw, currentHub: Warehouse): Vehicle {
        return Vehicle(
            id = vRaw.vehicleIds,
            maxCapacityKg = vRaw.maxCapacityKg,
            costPerKm = vRaw.costPerKm,
            currentHub = currentHub
        )
    }

    private fun attachPackagesToWarehouses(
        warehousesMap: Map<String, Warehouse>,
        packagesByOriginId: Map<String, List<PackageRaw>>
    ) {
        for ((hubId, originWarehouse) in warehousesMap) {
            val rawPackages = packagesByOriginId[hubId] ?: continue
            for (pRaw in rawPackages) {
                val destinationWarehouse = warehousesMap[pRaw.destinationHubId] ?: continue
                val pkg = createPackage(pRaw, originWarehouse, destinationWarehouse)
                originWarehouse.addPackage(pkg)
            }
        }
    }

    private fun createPackage(
        pRaw: PackageRaw,
        origin: Warehouse,
        destination: Warehouse
    ): Package {
        return Package(
            id = pRaw.packageId,
            weight = pRaw.weight,
            priority = pRaw.priority,
            origin = origin,
            destination = destination
        )
    }

    private fun attachRoutesToWarehouses(
        warehousesMap: Map<String, Warehouse>,
        routesByOriginId: Map<String, List<RouteRaw>>
    ) {
        for ((hubId, originWarehouse) in warehousesMap) {
            val rawRoutes = routesByOriginId[hubId] ?: continue
            for (rRaw in rawRoutes) {
                val destinationWarehouse = warehousesMap[rRaw.destinationHubId] ?: continue
                val route = createRoute(rRaw, originWarehouse, destinationWarehouse)
                originWarehouse.addRoute(route)
            }
        }
    }

    private fun createRoute(
        rRaw: RouteRaw,
        origin: Warehouse,
        destination: Warehouse
    ): Route {
        return Route(
            id = rRaw.routeId,
            distanceKm = rRaw.distanceKm,
            typicalDelayMin = rRaw.typicalDelayMin,
            origin = origin,
            destination = destination
        )
    }
}
