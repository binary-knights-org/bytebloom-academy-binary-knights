package domain.builder

import domain.model.Warehouse

class DomainGraphBuilder(
    private val repositories: RepositoryProvider,
) {

    private val warehousesId: Map<String, Warehouse> = createWarehouseNodes()

    fun buildGraph(): List<Warehouse> {
        attachVehiclesToWarehouses(warehousesId)
        attachPackagesToWarehouses(warehousesId)
        attachRoutesToWarehouses(warehousesId)

        return warehousesId.values.toList()
    }

    private fun createWarehouseNodes(): Map<String, Warehouse> {
        return repositories.warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }
    }

    private fun attachVehiclesToWarehouses(
        warehousesById: Map<String, Warehouse>
    ) {
        val vehiclesGroupedByHubId = repositories.vehicleRepository
            .getAllVehicles()
            .groupBy { it.currentHub.id }

        for ((hubId, warehouse) in warehousesById) {
            val vehicles = vehiclesGroupedByHubId[hubId] ?: continue

            vehicles.forEach { vehicle ->
                warehouse.addVehicle(vehicle)
            }
        }
    }

    private fun attachPackagesToWarehouses(
        warehousesById: Map<String, Warehouse>
    ) {
        val packagesGroupedByOriginId = repositories.packageRepository
            .getAllPackages()
            .groupBy { it.originHub.id }

        for ((hubId, warehouse) in warehousesById) {
            val packages = packagesGroupedByOriginId[hubId] ?: continue

            packages.forEach { pkg ->
                warehouse.addPackage(pkg)
            }
        }
    }

    private fun attachRoutesToWarehouses(
        warehousesById: Map<String, Warehouse>
    ) {
        val routesGroupedByOriginId = repositories.routeRepository
            .getAllRoutes()
            .groupBy { it.originHub.id }

        for ((hubId, warehouse) in warehousesById) {
            val routes = routesGroupedByOriginId[hubId] ?: continue

            routes.forEach { route ->
                warehouse.addRoute(route)
            }
        }
    }
}
