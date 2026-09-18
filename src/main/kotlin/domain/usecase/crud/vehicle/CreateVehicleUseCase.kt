package domain.usecase.crud.vehicle

import domain.exception.EntityValidationException
import domain.model.Vehicle
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.vehicle.CreateVehicleValidator

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: CreateVehicleValidator
) {
    suspend operator fun invoke(vehicle: Vehicle): Boolean {
        if (validator.validate(vehicle) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot create vehicle: invalid vehicle data.")
        }

        return vehicleRepository.create(vehicle)
    }
}
