package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.exception.EntityNotFoundException
import domain.validator.ValidationResult
import domain.validator.vehicle.VehicleIdValidator

class GetVehicleByIdUseCase(
    private val vehicleRepository: VehicleRepository,
    private val idValidator: VehicleIdValidator
) {
    suspend operator fun invoke(id: String): ValidationResult<Vehicle> {
        val validationResult = idValidator.validate(id)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val vehicle = vehicleRepository.getById(id)
        return if (vehicle != null) {
            ValidationResult.Success(vehicle)
        } else {
            ValidationResult.Failure(listOf(EntityNotFoundException("Vehicle", id)))
        }
    }
}
