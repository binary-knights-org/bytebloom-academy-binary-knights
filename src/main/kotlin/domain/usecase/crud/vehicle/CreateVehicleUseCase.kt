package domain.usecase.crud.vehicle

import domain.model.Vehicle
import domain.model.input.CreateVehicleInput
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.vehicle.CreateVehicleValidator

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: CreateVehicleValidator
) {
    suspend operator fun invoke(input: CreateVehicleInput): ValidationResult {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid) return validation

        val vehicle = Vehicle(
            id = input.id,
            maxCapacityKg = input.maxCapacityKg,
            costPerKm = input.costPerKm,
            currentHub = input.currentHub
        )

        vehicleRepository.create(vehicle)

        return ValidationResult.Valid
    }
}
