package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.model.input.CreateVehicleInput
import domain.repository.VehicleRepository
import domain.model.exception.DatabaseOperationFailedException
import domain.validator.ValidationResult
import domain.validator.vehicle.CreateVehicleValidator

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: CreateVehicleValidator
) {
    suspend operator fun invoke(input: CreateVehicleInput): ValidationResult<Vehicle> {
        val validationResult = validator.validate(input)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val vehicle = Vehicle(
            id = input.id,
            maxCapacityKg = input.maxCapacityKg,
            costPerKm = input.costPerKm,
            currentHub = input.currentHub
        )

        val isCreated = vehicleRepository.create(vehicle)

        return if (isCreated) {
            ValidationResult.Success(vehicle)
        } else {
            ValidationResult.Failure(listOf(DatabaseOperationFailedException("create", "vehicle")))
        }
    }
}
