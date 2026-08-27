package domain.usecase

import domain.model.Vehicle
import domain.model.Warehouse

class AddVehicleToHubUseCase {
    operator fun invoke(vehicle: Vehicle, targetHub: Warehouse) {
        targetHub.addVehicle(vehicle)
    }
}
