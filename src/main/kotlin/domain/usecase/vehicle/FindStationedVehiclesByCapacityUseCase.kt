package domain.usecase.vehicle

import domain.model.Vehicle
import domain.repository.WarehouseRepository

class FindStationedVehiclesByCapacityUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(warehouseId: String, minCapacityKg: Double): List<Vehicle> {
        val warehouse = warehouseRepository.getAllWarehouses()
            .firstOrNull { it.id == warehouseId } ?: return emptyList()
        return warehouse.stationedVehicles.filter { it.maxCapacityKg >= minCapacityKg }
    }
}
