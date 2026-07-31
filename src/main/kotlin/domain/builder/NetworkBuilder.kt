package domain.builder

import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse

class NetworkBuilder {

    private val warehouseList = mutableListOf<Warehouse>()

    fun getAllWarehouses(): List<Warehouse> = warehouseList.toList()

    private fun findWarehouseById(hubId: String): Warehouse? {
        for (warehouse in warehouseList)
            if (warehouse.id == hubId) {
                return warehouse
            }
        return null
    }

    fun buildWarehouse(
        id: String,
        name: String,
        regionalZone: String
    ): Warehouse {
        val newWarehouse = Warehouse(id, name, regionalZone)
        warehouseList.add(newWarehouse)
        return newWarehouse
    }

    fun buildVehicle(id: String, maxCapacityKg: Double, costPerKm: Double, hubId: String): Vehicle? {
        val currentHub = findWarehouseById(hubId) ?: return null
        val newVehicle = Vehicle(id, maxCapacityKg, costPerKm, currentHub)
        currentHub.addVehicle(newVehicle)
        return newVehicle
    }

    fun buildRoute(
        id: String,
        distanceKm: Double?,
        typicalDelayMin: Int?,
        originId: String,
        destinationId: String
    ): Route? {
        val originHub = findWarehouseById(originId)
        val destinationHub = findWarehouseById(destinationId)
        if (originHub != null && destinationHub != null) {
            val newRoute = Route(id, distanceKm, typicalDelayMin, originHub, destinationHub)
            originHub.addRoute(newRoute)
            return newRoute
        }
        return null
    }

    fun buildPackage(
        id: String,
        weight: Double,
        priority: String,
        originId: String,
        destinationId: String
    ): Package? {
        val originHub = findWarehouseById(originId)
        val destinationHub = findWarehouseById(destinationId)
        if (originHub != null && destinationHub != null) {
            val newPackage = Package(id, weight, priority, originHub, destinationHub)
            originHub.addPackage(newPackage)
            return newPackage
        }
        return null
    }
}
