package domain.usecase.crud.warehouse

import domain.exception.EntityValidationException
import domain.model.Warehouse
import domain.model.input.UpdateWarehouseInput
import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.warehouse.UpdateWarehouseValidator
import domain.validator.warehouse.WarehouseIdValidator

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: WarehouseIdValidator,
    private val validator: UpdateWarehouseValidator
) {
    suspend operator fun invoke(input: UpdateWarehouseInput): Boolean {
        validateInput(input)

        val existing = warehouseRepository.getById(input.id)
            ?: throw EntityValidationException("Warehouse with ID '${input.id}' was not found.")

        val updated = Warehouse.create(
            id = existing.id,
            name = input.name ?: existing.name,
            regionalZone = input.regionalZone ?: existing.regionalZone,
            latitude = input.latitude ?: existing.latitude,
            longitude = input.longitude ?: existing.longitude
        )

        return warehouseRepository.update(updated)
    }

    private fun validateInput(input: UpdateWarehouseInput) {
        if (idValidator.validate(input.id) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot update warehouse: invalid warehouse ID.")
        }

        if (validator.validate(input) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot update warehouse: invalid warehouse data.")
        }
    }
}
