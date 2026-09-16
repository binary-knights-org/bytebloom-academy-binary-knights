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
        val idValidation = WarehouseValidator.validateId(warehouse.id)
        if (idValidation is ValidationResult.Failure) {
            return false
        }

        val fields = WarehouseUpdateFields(
            hubName = warehouse.name,
            regionalZone = warehouse.regionalZone,
            latitude = warehouse.latitude,
            longitude = warehouse.longitude
        )

        val fieldsValidation = WarehouseValidator.validateForUpdate(fields)
        if (fieldsValidation is ValidationResult.Failure) {
            return false
        }

        return warehouseRepository.updateWarehouse(warehouse)
    }
}
