package domain.usecase.vehicle

import domain.model.Vehicle
import domain.model.Warehouse

class AddVehicleToHubUseCase {

    operator fun invoke(
        warehouse: Warehouse,
        vehicle: Vehicle
    ) {
        if (warehouse.stationedVehicles.any { it.id == vehicle.id }) {
            throw IllegalStateException("Vehicle with id ${vehicle.id} already exists in this hub")
        }
        warehouse.addVehicle(vehicle)
    }
}
