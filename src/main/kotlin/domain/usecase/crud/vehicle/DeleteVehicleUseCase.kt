package domain.usecase.crud.vehicle

import domain.repository.VehicleRepository
import domain.exception.DatabaseOperationFailedException
import domain.validator.ValidationResult
import domain.validator.vehicle.VehicleIdValidator

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val idValidator: VehicleIdValidator
) {
    suspend operator fun invoke(id: String): ValidationResult<Unit> {
        val validationResult = idValidator.validate(id)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val isDeleted = vehicleRepository.delete(id)
        return if (isDeleted) {
            ValidationResult.Success(Unit)
        } else {
            ValidationResult.Failure(listOf(DatabaseOperationFailedException("delete", "vehicle")))
        }
    }
}
