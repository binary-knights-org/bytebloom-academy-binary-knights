package domain.usecase.crud.vehicle

import domain.exception.DatabaseConflictException
import domain.repository.VehicleRepository

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> {

        return runCatching { vehicleRepository.delete(id) }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) {
                    Result.success(Unit)
                } else {
                    Result.failure(DatabaseConflictException("Failed to delete vehicle with ID '$id' from database."))
                }
            },
            onFailure = { error ->
                Result.failure(DatabaseConflictException("Failed to delete vehicle: ${error.message}", error))
            }
        )
    }
}
