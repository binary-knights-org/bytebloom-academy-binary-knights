package domain.usecase.crud.vehicle

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.model.Vehicle
import domain.model.input.CreateVehicleInput
import domain.repository.VehicleRepository
import domain.validator.vehicle.CreateVehicleValidator

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: CreateVehicleValidator
) {
    suspend operator fun invoke(input: CreateVehicleInput): Result<Vehicle> {
        val validation = validator.validate(input)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

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
                            Result.failure(DatabaseConflictException("Failed to create vehicle in database."))
                        }
                    },
                    onFailure = { error ->
                        Result.failure(DatabaseConflictException("Failed to create vehicle: ${error.message}", error))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
