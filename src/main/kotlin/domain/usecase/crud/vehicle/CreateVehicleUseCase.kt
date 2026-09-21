package domain.usecase.crud.vehicle

import domain.model.exception.OperationFailedException
import data.exception.translateDataError
import domain.model.exception.EntityValidationException
import domain.model.Vehicle
import domain.model.input.CreateVehicleInput
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.vehicle.CreateVehicleValidator

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: CreateVehicleValidator
) {
    suspend operator fun invoke(input: CreateVehicleInput): Result<Vehicle> {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid)
            return Result.failure(EntityValidationException(validation.violations))

        return runCatching {
            Vehicle(
                id = input.id,
                maxCapacityKg = input.maxCapacityKg,
                costPerKm = input.costPerKm,
                currentHub = input.currentHub
            )
        }.fold(
            onSuccess = { vehicle ->
                runCatching { vehicleRepository.create(vehicle) }.fold(
                    onSuccess = { isCreated ->
                        if (isCreated) {
                            Result.success(vehicle)
                        } else {
                            Result.failure(OperationFailedException())
                        }
                    },
                    onFailure = { error ->
                        Result.failure(translateDataError(error, "create", "vehicle"))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
