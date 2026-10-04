package domain.usecase.vehicle

import domain.model.Vehicle
import domain.model.Warehouse

class AddVehicleToHubUseCase {

    operator fun invoke(
        warehouse: Warehouse,
        vehicle: Vehicle
    ) {
        warehouse.addVehicle(vehicle)
    }
}
