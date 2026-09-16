package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.VehicleUpdateFields
import domain.validator.VehicleValidator

class UpdateVehicleUseCase(private val vehicleRepository: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle): Vehicle? {
        val idValidation = VehicleValidator.validateId(vehicle.id)
        if (idValidation is ValidationResult.Failure) {
            return null
        }

        val fields = VehicleUpdateFields(
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm,
            currentHubId = vehicle.currentHub.id
        )

        val fieldsValidation = VehicleValidator.validateForUpdate(fields)
        if (fieldsValidation is ValidationResult.Failure) {
            return null
        }

        return vehicleRepository.update(vehicle)
    }
}
