package domain.usecase.crud.warehouse

import domain.exception.EntityValidationException
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.warehouse.CreateWarehouseValidator

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: CreateWarehouseValidator
) {
    suspend operator fun invoke(warehouse: Warehouse): Boolean {
        if (validator.validate(warehouse) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot create warehouse: invalid warehouse data.")
        }

        return warehouseRepository.create(warehouse)
    }
}
