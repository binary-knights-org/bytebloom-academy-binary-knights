package domain.usecase.crud.warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.WarehouseValidator

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(warehouseId: String): Warehouse? {
        val validationResult = WarehouseValidator.validateId(warehouseId)
        if (validationResult is ValidationResult.Failure) {
            return null
        }

        return warehouseRepository.getWarehouseById(warehouseId)
    }
}
