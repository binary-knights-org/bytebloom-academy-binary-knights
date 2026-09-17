package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.VehicleUpdateFields
import domain.validator.VehicleValidator

class UpdateVehicleUseCase(private val vehicleRepository: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle): Boolean {
        val fields = VehicleUpdateFields(
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm,
            currentHubId = vehicle.currentHub.id
        )

        val isVaild = VehicleValidator.validateForUpdate(fields) is ValidationResult.Success &&
                VehicleValidator.validateId(vehicle.id) is ValidationResult.Success

        return isVaild && vehicleRepository.update(vehicle)
    }
}

