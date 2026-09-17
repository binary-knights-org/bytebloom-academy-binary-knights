package domain.usecase.crud.warehouse

import domain.exception.EntityValidationException
import domain.exception.ResourceNotFoundException
import domain.repository.WarehouseRepository
import domain.validator.warehouse.ValidationResult
import domain.validator.ValidateWarehouseIdUseCase

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: ValidateWarehouseIdUseCase = ValidateWarehouseIdUseCase()
) {
    suspend operator fun invoke(id: String): Result<Boolean> {
        val validation = idValidator(id)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(EntityValidationException(validation.violations))
        }

        return runCatching {
            val deleted = warehouseRepository.delete(id)
            if (!deleted) {
                throw ResourceNotFoundException("Cannot delete warehouse with ID '$id': Resource not found")
            }
            true
        }
    }
}
