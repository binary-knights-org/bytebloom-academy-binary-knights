package domain.usecase

import domain.model.Vehicle
import domain.repository.VehicleRepository

class GetAllVehiclesUseCase(
    private val vehicleRepository: VehicleRepository
) {
    operator fun invoke(): List<Vehicle> {
        return vehicleRepository.getAllVehicles()
    }
}
