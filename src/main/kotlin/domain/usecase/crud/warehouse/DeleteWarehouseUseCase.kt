package domain.usecase.crud.warehouse

import domain.exception.EntityValidationException
import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.warehouse.WarehouseIdValidator

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: WarehouseIdValidator
) {
    suspend operator fun invoke(id: String): Boolean {
        if (idValidator.validate(id) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot delete warehouse: invalid warehouse ID.")
        }

        return warehouseRepository.delete(id)
    }
}
