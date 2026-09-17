package domain.usecase.crud.warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.WarehouseUpdateFields
import domain.validator.WarehouseValidator

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(warehouse: Warehouse): Boolean {
        val fields = WarehouseUpdateFields(
            hubName = warehouse.name,
            regionalZone = warehouse.regionalZone,
            latitude = warehouse.latitude,
            longitude = warehouse.longitude
        )

        val isValid = WarehouseValidator.validateId(warehouse.id) is ValidationResult.Success &&
                WarehouseValidator.validateForUpdate(fields) is ValidationResult.Success

        return isValid && warehouseRepository.update(warehouse)
    }
}
