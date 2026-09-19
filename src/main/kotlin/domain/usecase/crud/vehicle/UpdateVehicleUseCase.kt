package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.model.input.UpdateVehicleInput
import domain.repository.VehicleRepository
import domain.model.exception.DatabaseOperationFailedException
import domain.model.exception.EntityNotFoundException
import domain.validator.ValidationResult
import domain.validator.vehicle.UpdateVehicleValidator

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository, private val validator: UpdateVehicleValidator
) {
    suspend operator fun invoke(input: UpdateVehicleInput): ValidationResult<Vehicle> {
        val validationResult = validator.validate(input)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        return executeUpdate(input)
    }

    private suspend fun executeUpdate(input: UpdateVehicleInput): ValidationResult<Vehicle> {
        val existingVehicle = vehicleRepository.getById(input.id) ?: return ValidationResult.Failure(
            listOf(EntityNotFoundException("Vehicle", input.id))
        )

        val updatedVehicle = existingVehicle.copy(
            maxCapacityKg = input.maxCapacityKg ?: existingVehicle.maxCapacityKg,
            costPerKm = input.costPerKm ?: existingVehicle.costPerKm,
            currentHub = input.currentHub ?: existingVehicle.currentHub
        )

        val isUpdated = vehicleRepository.update(updatedVehicle)

        return if (isUpdated) ValidationResult.Success(updatedVehicle)
        else ValidationResult.Failure(listOf(DatabaseOperationFailedException("update", "vehicle")))
    }
}
