package domain.usecase.crud.warehouse

import domain.model.exception.OperationFailedException
import data.exception.translateDataError
import domain.model.exception.EntityValidationException
import domain.model.exception.ResourceNotFoundException
import domain.model.Warehouse
import domain.model.input.UpdateWarehouseInput
import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.warehouse.UpdateWarehouseValidator
import domain.model.RegionalZone

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: UpdateWarehouseValidator
) {
    suspend operator fun invoke(input: UpdateWarehouseInput): Result<Warehouse> {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid)
            return Result.failure(EntityValidationException(validation.violations))

        return runCatching { warehouseRepository.getById(input.id) }.fold(
            onSuccess = { existing ->
                if (existing == null) {
                    Result.failure(ResourceNotFoundException())
                } else {
                    executeUpdate(existing, input)
                }
            },
            onFailure = { error ->
                Result.failure(translateDataError(error, "retrieve", "warehouse"))
            }
        )
    }

    private suspend fun executeUpdate(existing: Warehouse, input: UpdateWarehouseInput): Result<Warehouse> {
        return runCatching {
            existing.copy(
                name = input.name ?: existing.name,
                regionalZone = input.regionalZone?.let { RegionalZone.valueOf(it.trim().uppercase()) }
                    ?: existing.regionalZone,
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
                            Result.failure(OperationFailedException())
                        }
                    },
                    onFailure = { error ->
                        Result.failure(translateDataError(error, "update", "warehouse"))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
