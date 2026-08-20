package domain.builder

import data.dataholder.PackageRaw
import data.dataholder.RouteRaw
import data.dataholder.VehicleRaw
import data.dataholder.WarehouseRaw
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse

class DomainGraph(
    private val repositories: RepositoryProvider
) {

    fun createWarehouseNodes(): Map<String, Warehouse> {
        return repositories.warehouseRepository.getAllWarehouses().associateBy(
            keySelector = { it.hubId },
            valueTransform = { createWarehouse(it) }
        )
    }

    fun attachVehiclesToWarehouses(warehousesId: Map<String, Warehouse>) {
        val vehiclesGroupByHubId = repositories.vehicleRepository.getAllVehicles()
            .groupBy { it.currentHubId }

        for ((hubId, warehouse) in warehousesId) {
            val rawFleet = vehiclesGroupByHubId[hubId] ?: continue
            populateWarehouseVehicles(warehouse, rawFleet)
        }
    }

    fun attachPackagesToWarehouses(warehousesId: Map<String, Warehouse>) {
        val packagesGroupByOriginId = repositories.packageRepository.getAllPackages()
            .groupBy { it.originHubId }

        for ((hubId, originWarehouse) in warehousesId) {
            val rawPackages = packagesGroupByOriginId[hubId] ?: continue
            populateWarehousePackages(originWarehouse, rawPackages, warehousesId)
        }
    }

    fun attachRoutesToWarehouses(warehousesId: Map<String, Warehouse>) {
        val routesGroupByOriginId = repositories.routeRepository.getAllRoutes()
            .groupBy { it.originHubId }

        for ((hubId, originWarehouse) in warehousesId) {
            val rawRoutes = routesGroupByOriginId[hubId] ?: continue
            populateWarehouseRoutes(originWarehouse, rawRoutes, warehousesId)
        }
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

    private fun populateWarehouseVehicles(warehouse: Warehouse, rawVehicles: List<VehicleRaw>) {
        for (rawVehicle in rawVehicles) {
            val vehicle = createVehicle(rawVehicle, warehouse)
            warehouse.addVehicle(vehicle)
        }
    }

    private fun createVehicle(rawVehicle: VehicleRaw, currentHub: Warehouse): Vehicle {
        return Vehicle(
            id = rawVehicle.vehicleIds.first(),
            maxCapacityKg = rawVehicle.maxCapacityKg,
            costPerKm = rawVehicle.costPerKm,
            currentHub = currentHub
        )
    }

    private fun populateWarehousePackages(
        originWarehouse: Warehouse,
        rawPackages: List<PackageRaw>,
        warehousesId: Map<String, Warehouse>
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

    private fun populateWarehouseRoutes(
        originWarehouse: Warehouse,
        rawRoutes: List<RouteRaw>,
        warehousesId: Map<String, Warehouse>
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
