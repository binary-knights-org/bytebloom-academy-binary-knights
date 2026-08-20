package domain.builder

import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

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

    private fun createWarehouse(record: WarehouseRepository.WarehouseRecord): Warehouse {
        return Warehouse(
            id = record.hubId,
            name = record.hubName,
            regionalZone = record.regionalZone,
            latitude = record.latitude,
            longitude = record.longitude
        )
    }

    private fun populateWarehouseVehicles(
        warehouse: Warehouse,
        records: List<VehicleRepository.VehicleRecord>
    ) {
        for (record in records) {
            val vehicle = createVehicle(record, warehouse)
            warehouse.addVehicle(vehicle)
        }
    }

    private fun createVehicle(
        record: VehicleRepository.VehicleRecord,
        currentHub: Warehouse
    ): Vehicle {
        return Vehicle(
            id = record.vehicleIds.first(),
            maxCapacityKg = record.maxCapacityKg,
            costPerKm = record.costPerKm,
            currentHub = currentHub
        )
    }

    private fun populateWarehousePackages(
        originWarehouse: Warehouse,
        records: List<PackageRepository.PackageRecord>,
        warehousesId: Map<String, Warehouse>
    ) {
        for (record in records) {
            val destinationWarehouse = warehousesId[record.destinationHubId] ?: continue
            val pkg = createPackage(record, originWarehouse, destinationWarehouse)
            originWarehouse.addPackage(pkg)
        }
    }

    private fun createPackage(
        record: PackageRepository.PackageRecord,
        origin: Warehouse,
        destination: Warehouse
    ): Package {
        return Package(
            id = record.packageId,
            weight = record.weight,
            priority = record.priority,
            originHub = origin,
            destinationHub = destination
        )
    }

    private fun populateWarehouseRoutes(
        originWarehouse: Warehouse,
        records: List<RouteRepository.RouteRecord>,
        warehousesId: Map<String, Warehouse>
    ) {
        for (record in records) {
            val destinationWarehouse = warehousesId[record.destinationHubId] ?: continue
            val route = createRoute(record, originWarehouse, destinationWarehouse)
            originWarehouse.addRoute(route)
        }
    }

    private fun createRoute(
        record: RouteRepository.RouteRecord,
        origin: Warehouse,
        destination: Warehouse
    ): Route {
        return Route(
            id = record.routeId,
            distanceKm = record.distanceKm,
            typicalDelayMin = record.typicalDelayMin,
            originHub = origin,
            destinationHub = destination
        )
    }
}
