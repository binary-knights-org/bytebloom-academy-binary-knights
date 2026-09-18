package domain.usecase.crud.vehicle

import domain.exception.EntityValidationException
import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.vehicle.VehicleIdValidator

class GetVehicleByIdUseCase(
    private val vehicleRepository: VehicleRepository,
    private val idValidator: VehicleIdValidator
) {
    suspend operator fun invoke(id: String): Vehicle? {
        if (idValidator.validate(id) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot get vehicle: invalid vehicle ID.")
        }

        return vehicleRepository.getById(id)
    }
}
