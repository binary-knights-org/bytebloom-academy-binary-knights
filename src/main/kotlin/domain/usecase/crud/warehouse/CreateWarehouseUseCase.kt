package domain.usecase.crud.warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.WarehouseCreateFields
import domain.validator.WarehouseValidator

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

        val isValid = WarehouseValidator.validateForCreate(fields) is ValidationResult.Success
        return isValid && warehouseRepository.create(warehouse)
    }
}
