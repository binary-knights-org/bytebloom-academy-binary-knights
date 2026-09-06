package domain.builder

import domain.model.Warehouse
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository

class DomainGraphBuilder(
    private val warehouseRepository: WarehouseRepository,
    private val vehicleRepository: VehicleRepository,
    private val packageRepository: PackageRepository,
    private val routeRepository: RouteRepository
) {

    private val warehousesById: Map<String, Warehouse> = createWarehouseNodes()

    fun buildGraph(): List<Warehouse> {
        linkDataToWarehouse(
            warehousesById,
            vehicleRepository.getAllVehicles().groupBy { it.currentHub.id }
        ) { warehouse, vehicle ->
            warehouse.addVehicle(vehicle)
        }

        linkDataToWarehouse(
            warehousesById,
            packageRepository.getAllPackages().groupBy { it.originHub.id }
        ) { warehouse, pkg ->
            warehouse.addPackage(pkg)
        }

        linkDataToWarehouse(
            warehousesById,
            routeRepository.getAllRoutes().groupBy { it.originHub.id }
        ) { warehouse, route ->
            warehouse.addRoute(route)
        }

        return warehousesById.values.toList()
    }

    private fun createWarehouseNodes(): Map<String, Warehouse> {
        return warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }
    }

    private fun <T> linkDataToWarehouse(
        warehousesById: Map<String, Warehouse>,
        dataByHubId: Map<String, List<T>>,
        addToWarehouse: (Warehouse, T) -> Unit
    ) {
        for ((hubId, warehouse) in warehousesById) {
            val data = dataByHubId[hubId] ?: continue

            data.forEach { item ->
                addToWarehouse(warehouse, item)
            }
        }
    }
}
