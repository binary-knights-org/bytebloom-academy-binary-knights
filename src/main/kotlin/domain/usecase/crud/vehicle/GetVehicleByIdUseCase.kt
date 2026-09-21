package domain.usecase.crud.vehicle

import data.exception.translateDataError
import domain.model.exception.ResourceNotFoundException
import domain.model.Vehicle
import domain.repository.VehicleRepository

class GetVehicleByIdUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: String): Result<Vehicle> {
        return runCatching { vehicleRepository.getById(id) }.fold(
            onSuccess = { vehicle ->
                if (vehicle != null) {
                    Result.success(vehicle)
                } else {
                    Result.failure(ResourceNotFoundException())
                }
            },
            onFailure = { error ->
                Result.failure(translateDataError(error, "fetch", "vehicle"))
            }
        )
    }
}
