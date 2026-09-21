package domain.usecase.crud.vehicle

import domain.model.exception.OperationFailedException
import data.exception.translateDataError
import domain.model.exception.EntityValidationException
import domain.model.exception.ResourceNotFoundException
import domain.model.Vehicle
import domain.model.input.UpdateVehicleInput
import domain.repository.VehicleRepository
import domain.validator.ValidationResult
import domain.validator.vehicle.UpdateVehicleValidator

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: UpdateVehicleValidator
) {
    suspend operator fun invoke(input: UpdateVehicleInput): Result<Vehicle> {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid)
            return Result.failure(EntityValidationException(validation.violations))

        return runCatching { vehicleRepository.getById(input.id) }.fold(
            onSuccess = { existing ->
                if (existing == null) {
                    Result.failure(ResourceNotFoundException())
                } else {
                    executeUpdate(existing, input)
                }
            },
            onFailure = { error ->
                Result.failure(translateDataError(error, "retrieve", "vehicle"))
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
                            Result.failure(OperationFailedException())
                        }
                    },
                    onFailure = { error ->
                        Result.failure(translateDataError(error, "update", "vehicle"))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
