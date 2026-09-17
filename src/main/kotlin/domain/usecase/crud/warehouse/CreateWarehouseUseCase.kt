package domain.usecase.crud.warehouse

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.validator.warehouse.ValidationResult
import domain.validator.warehouse.ValidateCreateWarehouseUseCase

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: ValidateCreateWarehouseUseCase = ValidateCreateWarehouseUseCase()
) {
    suspend operator fun invoke(warehouse: Warehouse): Result<Boolean> {
        val validation = validator(warehouse)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(EntityValidationException(validation.violations))
        }

        return runCatching {
            val created = warehouseRepository.create(warehouse)
            if (!created) {
                throw DatabaseConflictException("Warehouse with ID '${warehouse.id}'" +
                        " already exists or conflict occurred")
            }
            true
        }
    }
}
