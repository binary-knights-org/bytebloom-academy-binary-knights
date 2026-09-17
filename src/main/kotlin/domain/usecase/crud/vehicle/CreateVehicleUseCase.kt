package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.VehicleCreateFields
import domain.validator.VehicleValidator

class CreateVehicleUseCase(private val vehicleRepository: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle): Boolean {
        val fields = VehicleCreateFields(
            vehicleId = vehicle.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm,
            currentHubId = vehicle.currentHub.id
        )

        val isValid = VehicleValidator.validateForCreate(fields) is ValidationResult.Success
        return  isValid && vehicleRepository.create(vehicle)
    }
}

