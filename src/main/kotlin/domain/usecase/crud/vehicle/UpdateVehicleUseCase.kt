package domain.usecase.crud.vehicle

import domain.exception.ResourceNotFoundException
import domain.model.input.UpdateVehicleInput
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.vehicle.UpdateVehicleValidator

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: UpdateVehicleValidator
) {
    suspend operator fun invoke(input: UpdateVehicleInput): ValidationResult {
        val validation = validator.validate(input)
        if (validation.isInvalid) return validation

        val existingVehicle = vehicleRepository.getById(input.id)
            ?: throw ResourceNotFoundException("Vehicle with ID '${input.id}' was not found.")

        val updatedVehicle = existingVehicle.copy(
            maxCapacityKg = input.maxCapacityKg ?: existingVehicle.maxCapacityKg,
            costPerKm = input.costPerKm ?: existingVehicle.costPerKm,
            currentHub = input.currentHub ?: existingVehicle.currentHub
        )

        vehicleRepository.update(updatedVehicle)

        return ValidationResult.Valid
    }
}
