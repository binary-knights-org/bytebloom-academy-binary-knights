package domain.builder

import data.dataholder.FleetRaw
import data.dataholder.PackageRaw
import data.dataholder.RouteRaw
import data.dataholder.WarehouseRaw
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.PackageRepository
import domain.repository.WarehouseRepository

class DomainGraphBuilder(
    private val rawData: GraphData,
    private val warehouseRepository: WarehouseRepository,
    private val packageRepository: PackageRepository
) {

    private val warehousesId: Map<String, Warehouse> = createWarehouseNodes()

    fun buildGraph(): List<Warehouse> {
        attachVehiclesToWarehouses()
        attachPackagesToWarehouses()
        attachRoutesToWarehouses()

        return warehousesId.values.toList()
    }

    private fun groupVehiclesByHubId(): Map<String, List<FleetRaw>> {
        return rawData.rawFleet.groupBy { it.currentHubId }
    }

    private fun groupPackagesByOriginId(): Map<String, List<PackageRaw>> {

        return packageRepository.getPackages().groupBy { it.originHubId }
    }

    private fun groupRoutesByOriginId(): Map<String, List<RouteRaw>> {
        return rawData.rawRoutes.groupBy { it.originHubId }
    }

    private fun createWarehouseNodes(): Map<String, Warehouse> {
        return warehouseRepository.getWarehouses().associateBy(
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
        for ((hubId, warehouse) in warehousesId) {
            val rawFleet = groupVehiclesByHubId()[hubId] ?: continue
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
        for ((hubId, originWarehouse) in warehousesId) {
            val rawPackages = groupPackagesByOriginId()[hubId] ?: continue
            populateWarehousePackages(originWarehouse, rawPackages)
        }
    }

    private fun populateWarehousePackages(
        originWarehouse: Warehouse,
        rawPackages: List<PackageRaw>,
    ) {
        for (rawPackage in rawPackages) {
            val destinationWarehouse = warehousesId[rawPackage.destinationHubId] ?: continue
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
        for ((hubId, originWarehouse) in warehousesId) {
            val rawRoutes = groupRoutesByOriginId()[hubId] ?: continue
            populateWarehouseRoutes(originWarehouse, rawRoutes)
        }
    }

    private fun populateWarehouseRoutes(
        originWarehouse: Warehouse,
        rawRoutes: List<RouteRaw>,
    ) {
        for (rawRoute in rawRoutes) {
            val destinationWarehouse = warehousesId[rawRoute.destinationHubId] ?: continue
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