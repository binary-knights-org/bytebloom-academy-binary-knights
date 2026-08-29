package domain.usecase

import domain.model.Warehouse

private const val ZERO_CAPACITY = 0.0

class GetWarehouseLoadFactorUseCase {
    operator fun invoke(warehouse: Warehouse): Double {
        val cargoWeight = warehouse.cargoQueue.sumOf { it.weight }
        val fleetCapacity = warehouse.stationedVehicles.sumOf { it.maxCapacityKg }

        return if (fleetCapacity == ZERO_CAPACITY) ZERO_CAPACITY
        else cargoWeight / fleetCapacity
    }
}