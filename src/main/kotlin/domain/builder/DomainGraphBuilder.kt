package domain.builder

import dataholder.FleetRaw
import dataholder.PackageRaw
import dataholder.RouteRaw
import dataholder.WarehouseRaw
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse

class DomainGraphBuilder(private val rawData: GraphRawData) {

    private val warehousesById: Map<String, Warehouse> = createWarehouseNodes()
    private val vehiclesByHubId = rawData.rawFleet.groupBy { it.currentHubId }
    private val packagesByOriginId = rawData.rawPackages.groupBy { it.originHubId }
    private val routesByOriginId = rawData.rawRoutes.groupBy { it.originHubId }

    fun buildGraph(): List<Warehouse> {
        attachVehiclesToWarehouses()
        attachPackagesToWarehouses()
        attachRoutesToWarehouses()

        return warehousesById.values.toList()
    }

    private fun createWarehouseNodes(): Map<String, Warehouse> {
        return rawData.rawWarehouses.associateBy(
            keySelector = { it.hubId },
            valueTransform = { createWarehouse(it) }
        )
    }

    private fun createWarehouse(rawWarehouse: WarehouseRaw): Warehouse {
        return Warehouse(
            id = rawWarehouse.hubId,
            name = rawWarehouse.hubName,
            regionalZone = rawWarehouse.regionalZone,
            latitude = rawWarehouse.latitude,
            longitude = rawWarehouse.longitude
        )
    }

    private fun attachVehiclesToWarehouses() {
        for ((hubId, warehouse) in warehousesById) {
            val rawFleet = vehiclesByHubId[hubId] ?: continue
            populateWarehouseVehicles(warehouse, rawFleet)
        }
    }

    private fun populateWarehouseVehicles(warehouse: Warehouse, rawVehicles: List<FleetRaw>) {
        for (rawVehicle in rawVehicles) {
            val vehicle = createVehicle(rawVehicle, warehouse)
            warehouse.addVehicle(vehicle)
        }
    }

    private fun createVehicle(rawVehicle: FleetRaw, currentHub: Warehouse): Vehicle {
        return Vehicle(
            id = rawVehicle.vehicleIds.first(),
            maxCapacityKg = rawVehicle.maxCapacityKg,
            costPerKm = rawVehicle.costPerKm,
            currentHub = currentHub
        )
    }

    private fun attachPackagesToWarehouses() {
        for ((hubId, originWarehouse) in warehousesById) {
            val rawPackages = packagesByOriginId[hubId] ?: continue
            populateWarehousePackages(originWarehouse, rawPackages)
        }
    }

    private fun populateWarehousePackages(
        originWarehouse: Warehouse,
        rawPackages: List<PackageRaw>,
    ) {
        for (rawPackage in rawPackages) {
            val destinationWarehouse = warehousesById[rawPackage.destinationHubId] ?: continue
            val pkg = createPackage(rawPackage, originWarehouse, destinationWarehouse)
            originWarehouse.addPackage(pkg)
        }
    }

    private fun createPackage(
        rawPackage: PackageRaw,
        origin: Warehouse,
        destination: Warehouse
    ): Package {
        return Package(
            id = rawPackage.packageId,
            weight = rawPackage.weight,
            priority = rawPackage.priority,
            originHub = origin,
            destinationHub = destination
        )
    }

    private fun attachRoutesToWarehouses() {
        for ((hubId, originWarehouse) in warehousesById) {
            val rawRoutes = routesByOriginId[hubId] ?: continue
            populateWarehouseRoutes(originWarehouse, rawRoutes)
        }
    }

    private fun populateWarehouseRoutes(
        originWarehouse: Warehouse,
        rawRoutes: List<RouteRaw>,
    ) {
        for (rawRoute in rawRoutes) {
            val destinationWarehouse = warehousesById[rawRoute.destinationHubId] ?: continue
            val route = createRoute(rawRoute, originWarehouse, destinationWarehouse)
            originWarehouse.addRoute(route)
        }
    }

    private fun createRoute(
        rawRoute: RouteRaw,
        origin: Warehouse,
        destination: Warehouse
    ): Route {
        return Route(
            id = rawRoute.routeId,
            distanceKm = rawRoute.distanceKm,
            typicalDelayMin = rawRoute.typicalDelayMin,
            originHub = origin,
            destinationHub = destination
        )
    }
}
