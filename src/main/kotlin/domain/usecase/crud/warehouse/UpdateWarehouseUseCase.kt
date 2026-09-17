package domain.usecase.crud.warehouse

import domain.exception.EntityValidationException
import domain.exception.ResourceNotFoundException
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.validator.warehouse.ValidationResult
import domain.validator.warehouse.UpdateWarehouseParams
import domain.validator.warehouse.ValidateUpdateWarehouseUseCase

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: ValidateUpdateWarehouseUseCase = ValidateUpdateWarehouseUseCase()
) {
    suspend operator fun invoke(warehouse: Warehouse): Result<Boolean> {
        val validation = validator(warehouse)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(EntityValidationException(validation.violations))
        }

        return runCatching {
            val updated = warehouseRepository.update(warehouse)
            if (!updated) {
                throw ResourceNotFoundException("Cannot update warehouse with ID '${warehouse.id}': Resource not found")
            }
            true
        }
    }

    suspend operator fun invoke(params: UpdateWarehouseParams): Result<Boolean> {
        val validation = validator(params)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(EntityValidationException(validation.violations))
        }

        return runCatching {
            val existing = warehouseRepository.getById(params.id)
                ?: throw ResourceNotFoundException("Cannot update warehouse with ID '${params.id}': Resource not found")

            val merged = existing.copy(
                name = params.name ?: existing.name,
                regionalZone = params.regionalZone ?: existing.regionalZone,
                latitude = params.latitude ?: existing.latitude,
                longitude = params.longitude ?: existing.longitude
            )

            val updated = warehouseRepository.update(merged)
            if (!updated) {
                throw ResourceNotFoundException("Cannot update warehouse with ID '${params.id}': Resource not found")
            }
            true
        }
    }
}
