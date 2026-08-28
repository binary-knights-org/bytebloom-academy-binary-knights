package domain.usecase

import domain.model.Vehicle
import domain.model.Warehouse

class FindStationedVehiclesByCapacityUseCase {
    operator fun invoke(warehouse: Warehouse, minCapacityKg: Double): List<Vehicle> {
        return warehouse.stationedVehicles.filter { it.maxCapacityKg >= minCapacityKg }
    }
}
