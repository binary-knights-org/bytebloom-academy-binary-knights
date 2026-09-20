package domain.usecase.crud.warehouse

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.exception.ResourceNotFoundException
import domain.model.Warehouse
import domain.model.input.UpdateWarehouseInput
import domain.repository.WarehouseRepository
import domain.validator.warehouse.UpdateWarehouseValidator

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: UpdateWarehouseValidator
) {
    suspend operator fun invoke(input: UpdateWarehouseInput): Result<Warehouse> {
        val validation = validator.validate(input)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

        return runCatching { warehouseRepository.getById(input.id) }.fold(
            onSuccess = { existing ->
                if (existing == null) {
                    Result.failure(ResourceNotFoundException("Warehouse with ID '${input.id}' was not found."))
                } else {
                    executeUpdate(existing, input)
                }
            },
            onFailure = { error ->
                Result.failure(
                    DatabaseConflictException(
                        "Failed to retrieve warehouse for update: ${error.message}", error
                    )
                )
            }
        )
    }

    private suspend fun executeUpdate(existing: Warehouse, input: UpdateWarehouseInput): Result<Warehouse> {
        return runCatching {
            existing.copy(
                name = input.name ?: existing.name,
                regionalZone = input.regionalZone ?: existing.regionalZone,
                latitude = input.latitude ?: existing.latitude,
                longitude = input.longitude ?: existing.longitude
            )
        }.fold(
            onSuccess = { updatedWarehouse ->
                runCatching { warehouseRepository.update(updatedWarehouse) }.fold(
                    onSuccess = { isUpdated ->
                        if (isUpdated) {
                            Result.success(updatedWarehouse)
                        } else {
                            Result.failure(DatabaseConflictException("Failed to update warehouse in database."))
                        }
                    },
                    onFailure = { error ->
                        Result.failure(DatabaseConflictException("Failed to update warehouse: ${error.message}", error))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
