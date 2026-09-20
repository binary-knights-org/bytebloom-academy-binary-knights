package domain.usecase.crud.vehicle

import domain.exception.DatabaseConflictException
import domain.exception.ResourceNotFoundException
import domain.model.Vehicle
import domain.repository.VehicleRepository

class GetVehicleByIdUseCase(
    private val vehicleRepository: VehicleRepository,
) {
    suspend operator fun invoke(id: String): Result<Vehicle> {

        return runCatching { vehicleRepository.getById(id) }.fold(
            onSuccess = { vehicle ->
                if (vehicle != null) {
                    Result.success(vehicle)
                } else {
                    Result.failure(ResourceNotFoundException("Vehicle with ID '$id' was not found."))
                }
            },
            onFailure = { error ->
                Result.failure(
                    DatabaseConflictException(
                        "Failed to fetch vehicle with ID '$id': ${error.message}", error))
            }
        )
    }
}
