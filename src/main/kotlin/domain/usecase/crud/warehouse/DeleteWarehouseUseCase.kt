package domain.usecase.crud.warehouse

import domain.repository.WarehouseRepository
import domain.exception.DatabaseOperationFailedException
import domain.validator.ValidationResult
import domain.validator.warehouse.WarehouseIdValidator

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: WarehouseIdValidator
) {
    suspend operator fun invoke(id: String): ValidationResult<Unit> {
        val validationResult = idValidator.validate(id)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val isDeleted = warehouseRepository.delete(id)
        return if (isDeleted) {
            ValidationResult.Success(Unit)
        } else {
            ValidationResult.Failure(listOf(DatabaseOperationFailedException("delete", "warehouse")))
        }
    }
}
