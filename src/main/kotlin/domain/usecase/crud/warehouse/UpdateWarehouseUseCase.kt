package domain.usecase.crud.warehouse

import domain.exception.ResourceNotFoundException
import domain.model.input.UpdateWarehouseInput
import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.warehouse.UpdateWarehouseValidator

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: UpdateWarehouseValidator
) {
    suspend operator fun invoke(input: UpdateWarehouseInput): ValidationResult {
        val validation = validator.validate(input)
        if (validation.isInvalid) return validation

        val existingWarehouse = warehouseRepository.getById(input.id)
            ?: throw ResourceNotFoundException("Warehouse with ID '${input.id}' was not found.")

        val updatedWarehouse = existingWarehouse.copy(
            name = input.name ?: existingWarehouse.name,
            regionalZone = input.regionalZone ?: existingWarehouse.regionalZone,
            latitude = input.latitude ?: existingWarehouse.latitude,
            longitude = input.longitude ?: existingWarehouse.longitude
        )

        warehouseRepository.update(updatedWarehouse)

        return ValidationResult.Valid
    }
}
