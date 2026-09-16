package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.VehicleCreateFields
import domain.validator.VehicleValidator

class CreateVehicleUseCase(private val vehicleRepository: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle): Vehicle? {
        val fields = VehicleCreateFields(
            vehicleId = vehicle.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm,
            currentHubId = vehicle.currentHub.id
        )

        val validationResult = VehicleValidator.validateForCreate(fields)
        if (validationResult is ValidationResult.Failure) {
            return null
        }

        return vehicleRepository.create(vehicle)
    }
}
