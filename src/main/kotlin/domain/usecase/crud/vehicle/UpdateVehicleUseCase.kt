package domain.usecase.crud.vehicle

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.exception.ResourceNotFoundException
import domain.model.Vehicle
import domain.model.input.UpdateVehicleInput
import domain.repository.VehicleRepository
import domain.validator.vehicle.UpdateVehicleValidator

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: UpdateVehicleValidator
) {
    suspend operator fun invoke(input: UpdateVehicleInput): Result<Vehicle> {
        val validation = validator.validate(input)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

        return runCatching { vehicleRepository.getById(input.id) }.fold(
            onSuccess = { existing ->
                if (existing == null) {
                    Result.failure(ResourceNotFoundException("Vehicle with ID '${input.id}' was not found."))
                } else {
                    executeUpdate(existing, input)
                }
            },
            onFailure = { error ->
                Result.failure(
                    DatabaseConflictException(
                        "Failed to retrieve vehicle for update: ${error.message}", error))
            }
        )
    }

    private suspend fun executeUpdate(existing: Vehicle, input: UpdateVehicleInput): Result<Vehicle> {
        return runCatching {
            existing.copy(
                maxCapacityKg = input.maxCapacityKg ?: existing.maxCapacityKg,
                costPerKm = input.costPerKm ?: existing.costPerKm,
                currentHub = input.currentHub ?: existing.currentHub
            )
        }.fold(
            onSuccess = { updatedVehicle ->
                runCatching { vehicleRepository.update(updatedVehicle) }.fold(
                    onSuccess = { isUpdated ->
                        if (isUpdated) {
                            Result.success(updatedVehicle)
                        } else {
                            Result.failure(DatabaseConflictException("Failed to update vehicle in database."))
                        }
                    },
                    onFailure = { error ->
                        Result.failure(DatabaseConflictException("Failed to update vehicle: ${error.message}", error))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
