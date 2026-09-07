package domain.usecase

import domain.repository.WarehouseRepository

private const val ZERO_CAPACITY = 0.0

class GetWarehouseLoadFactorUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(warehouseId: String): Double {
        val warehouse = warehouseRepository.getAllWarehouses()
            .firstOrNull { it.id == warehouseId } ?: return ZERO_CAPACITY

        val cargoWeight = warehouse.cargoQueue.sumOf { it.weight }
        val fleetCapacity = warehouse.stationedVehicles.sumOf { it.maxCapacityKg }

        return if (fleetCapacity == ZERO_CAPACITY) ZERO_CAPACITY
        else cargoWeight / fleetCapacity
    }
}
