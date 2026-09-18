package domain.usecase.crud.vehicle

import domain.exception.EntityValidationException
import domain.model.Vehicle
import domain.model.input.UpdateVehicleInput
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.vehicle.UpdateVehicleValidator
import domain.validator.vehicle.VehicleIdValidator

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val idValidator: VehicleIdValidator,
    private val validator: UpdateVehicleValidator
) {
    suspend operator fun invoke(input: UpdateVehicleInput): Boolean {
        validateInput(input)

        val existing = vehicleRepository.getById(input.id)
            ?: throw EntityValidationException("Vehicle with ID '${input.id}' was not found.")

        val updated = Vehicle.create(
            id = existing.id,
            maxCapacityKg = input.maxCapacityKg ?: existing.maxCapacityKg,
            costPerKm = input.costPerKm ?: existing.costPerKm,
            currentHub = input.currentHub ?: existing.currentHub
        )

        return vehicleRepository.update(updated)
    }

    private fun validateInput(input: UpdateVehicleInput) {
        if (idValidator.validate(input.id) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot update vehicle: invalid vehicle ID.")
        }

        if (validator.validate(input) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot update vehicle: invalid vehicle data.")
        }
    }
}
