package domain.usecase

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class GetActiveFleetCostUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke() : Double {
    return warehouseRepository.getAllWarehouses()
        .flatMap { it.stationedVehicles }
        .sumOf { it.costPerKm }
    }
}
