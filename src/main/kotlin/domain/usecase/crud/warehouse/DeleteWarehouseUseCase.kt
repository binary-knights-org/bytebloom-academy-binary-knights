package domain.usecase.crud.warehouse

import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.WarehouseValidator

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        val validationResult = WarehouseValidator.validateId(id)
        if (validationResult is ValidationResult.Failure) {
            return false
        }

        return warehouseRepository.deleteWarehouse(id)
    }
}
