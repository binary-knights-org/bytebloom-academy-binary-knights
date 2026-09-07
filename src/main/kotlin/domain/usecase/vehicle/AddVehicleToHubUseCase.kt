package domain.usecase.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository

class AddVehicleToHubUseCase(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(vehicle: Vehicle): Boolean {
        return vehicleRepository.addVehicleToHub(vehicle)
    }
}
