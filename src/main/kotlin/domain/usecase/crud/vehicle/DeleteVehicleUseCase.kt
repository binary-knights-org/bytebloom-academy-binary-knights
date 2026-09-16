package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.VehicleValidator

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(vehicle: Vehicle): Vehicle? {
        val validationResult = VehicleValidator.validateId(vehicle.id)
        if (validationResult is ValidationResult.Failure) {
            return null
        }

        return vehicleRepository.delete(vehicle)
    }
}
