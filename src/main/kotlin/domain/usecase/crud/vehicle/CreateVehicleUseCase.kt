package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.model.exception.EntityValidationException
import domain.model.input.CreateVehicleInput
import domain.model.validation.ValidationResult
import domain.repository.VehicleRepository
import domain.validator.vehicle.CreateVehicleValidator

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: CreateVehicleValidator
) {
    suspend operator fun invoke(input: CreateVehicleInput): Result<Vehicle> {
        val validation = validator.validate(input)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                EntityValidationException(validation.violations)
            )
        }

        return runCatching {
            Vehicle(
                id = input.id,
                maxCapacityKg = input.maxCapacityKg,
                costPerKm = input.costPerKm,
                currentHub = input.currentHub
            )
        }.mapCatching { vehicle ->
            vehicleRepository.create(vehicle)
            vehicle
        }
    }
}
