package domain.usecase.crud.warehouse

import domain.model.Warehouse
import domain.model.input.CreateWarehouseInput
import domain.repository.WarehouseRepository
import domain.model.exception.DatabaseOperationFailedException
import domain.validator.ValidationResult
import domain.validator.warehouse.CreateWarehouseValidator

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: CreateWarehouseValidator
) {
    suspend operator fun invoke(input: CreateWarehouseInput): ValidationResult<Warehouse> {
        val validationResult = validator.validate(input)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val warehouse = Warehouse(
            id = input.id,
            name = input.name,
            regionalZone = input.regionalZone,
            latitude = input.latitude,
            longitude = input.longitude
        )

        val isCreated = warehouseRepository.create(warehouse)

        return if (isCreated) {
            ValidationResult.Success(warehouse)
        } else {
            ValidationResult.Failure(listOf(DatabaseOperationFailedException("create", "warehouse")))
        }
    }
}
