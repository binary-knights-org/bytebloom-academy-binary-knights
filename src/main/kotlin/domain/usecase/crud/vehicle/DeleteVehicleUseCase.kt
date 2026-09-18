package domain.usecase.crud.vehicle

import domain.exception.EntityValidationException
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.vehicle.VehicleIdValidator

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val idValidator: VehicleIdValidator
) {
    suspend operator fun invoke(id: String): Boolean {
        if (idValidator.validate(id) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot delete vehicle: invalid vehicle ID.")
        }

        return vehicleRepository.delete(id)
    }
}
