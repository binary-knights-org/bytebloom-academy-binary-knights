package domain.usecase.crud.warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.validator.WarehouseCreateFields
import domain.validator.WarehouseValidator
import domain.validator.ValidationResult

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(warehouse: Warehouse): Boolean {
        val fields = WarehouseCreateFields(
            hubId = warehouse.id,
            hubName = warehouse.name,
            regionalZone = warehouse.regionalZone,
            latitude = warehouse.latitude,
            longitude = warehouse.longitude
        )

        val validationResult = WarehouseValidator.validateForCreate(fields)
        if (validationResult is ValidationResult.Failure) {
            return false
        }

        return warehouseRepository.createWarehouse(warehouse)
    }
}
