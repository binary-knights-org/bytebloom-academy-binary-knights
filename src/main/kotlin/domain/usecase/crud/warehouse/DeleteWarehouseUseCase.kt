package domain.usecase.crud.warehouse

import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.WarehouseValidator

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        val isValid = WarehouseValidator.validateId(id) is ValidationResult.Success
        return isValid && warehouseRepository.delete(id)
    }
}
