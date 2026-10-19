package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.model.exception.ResourceNotFoundException
import domain.repository.VehicleRepository

class GetVehicleByIdUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: String): Result<Vehicle> =
        runCatching {
            vehicleRepository.getById(id)
                ?: throw ResourceNotFoundException()
        }
}
