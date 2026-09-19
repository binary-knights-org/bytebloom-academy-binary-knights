package domain.usecase.crud.warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.exception.EntityNotFoundException
import domain.validator.ValidationResult
import domain.validator.warehouse.WarehouseIdValidator

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: WarehouseIdValidator
) {
    suspend operator fun invoke(id: String): ValidationResult<Warehouse> {
        val validationResult = idValidator.validate(id)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val warehouse = warehouseRepository.getById(id)
        return if (warehouse != null) {
            ValidationResult.Success(warehouse)
        } else {
            ValidationResult.Failure(listOf(EntityNotFoundException("Warehouse", id)))
        }
    }
}
