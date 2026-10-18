package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.model.exception.EntityValidationException
import domain.model.exception.ResourceNotFoundException
import domain.model.input.UpdateVehicleInput
import domain.model.validation.ValidationResult
import domain.repository.VehicleRepository
import domain.validator.vehicle.UpdateVehicleValidator

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: UpdateVehicleValidator
) {
    suspend operator fun invoke(input: UpdateVehicleInput): Result<Vehicle> {
        val validation = validator.validate(input)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                EntityValidationException(validation.violations)
            )
        }

        return runCatching {
            val existing = vehicleRepository.getById(input.id)
                ?: throw ResourceNotFoundException()

            val updated = existing.copy(
                maxCapacityKg = input.maxCapacityKg
                    ?: existing.maxCapacityKg,
                costPerKm = input.costPerKm ?: existing.costPerKm,
                currentHub = input.currentHub ?: existing.currentHub
            )

            vehicleRepository.update(updated)
            updated
        }
    }
}
