package domain.usecase.crud.warehouse

import domain.model.input.CreateWarehouseInput
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.warehouse.CreateWarehouseValidator

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: CreateWarehouseValidator
) {
    suspend operator fun invoke(input: CreateWarehouseInput): ValidationResult {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid) {
            return validation
        }
        val warehouse = Warehouse(
            id = input.id,
            name = input.name,
            regionalZone = input.regionalZone,
            latitude = input.latitude,
            longitude = input.longitude
        )
        warehouseRepository.create(warehouse)
        return ValidationResult.Valid
    }
}
