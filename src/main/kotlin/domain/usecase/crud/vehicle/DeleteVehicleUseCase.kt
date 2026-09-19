package domain.usecase.crud.vehicle

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.repository.VehicleRepository
import domain.validator.vehicle.VehicleIdValidator

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val idValidator: VehicleIdValidator
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        val validation = idValidator.validate(id)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

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
