package domain.usecase.crud.warehouse

import domain.model.Warehouse
import domain.model.input.UpdateWarehouseInput
import domain.repository.WarehouseRepository
import domain.model.exception.DatabaseOperationFailedException
import domain.model.exception.EntityNotFoundException
import domain.validator.ValidationResult
import domain.validator.warehouse.UpdateWarehouseValidator

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository, private val validator: UpdateWarehouseValidator
) {
    suspend operator fun invoke(input: UpdateWarehouseInput): ValidationResult<Warehouse> {
        val validationResult = validator.validate(input)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        return executeUpdate(input)
    }

    private suspend fun executeUpdate(input: UpdateWarehouseInput): ValidationResult<Warehouse> {
        val existingWarehouse = warehouseRepository.getById(input.id) ?: return ValidationResult.Failure(
            listOf(EntityNotFoundException("Warehouse", input.id))
        )

        val updatedWarehouse = existingWarehouse.copy(
            name = input.name ?: existingWarehouse.name,
            regionalZone = input.regionalZone ?: existingWarehouse.regionalZone,
            latitude = input.latitude ?: existingWarehouse.latitude,
            longitude = input.longitude ?: existingWarehouse.longitude
        )

        val isUpdated = warehouseRepository.update(updatedWarehouse)

        return if (isUpdated) ValidationResult.Success(updatedWarehouse)
        else ValidationResult.Failure(listOf(DatabaseOperationFailedException("update", "warehouse")))
    }
}
