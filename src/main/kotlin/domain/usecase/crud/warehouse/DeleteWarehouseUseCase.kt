package domain.usecase.crud.warehouse

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.repository.WarehouseRepository
import domain.validator.warehouse.WarehouseIdValidator

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: WarehouseIdValidator
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        val validation = idValidator.validate(id)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

        return runCatching { warehouseRepository.delete(id) }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) {
                    Result.success(Unit)
                } else {
                    Result.failure(DatabaseConflictException("Failed to delete warehouse with ID '$id' from database."))
                }
            },
            onFailure = { error ->
                Result.failure(DatabaseConflictException("Failed to delete warehouse: ${error.message}", error))
            }
        )
    }
}
