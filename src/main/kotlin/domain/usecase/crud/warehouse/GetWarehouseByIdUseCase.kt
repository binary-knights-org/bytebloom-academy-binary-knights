package domain.usecase.crud.warehouse

import domain.exception.EntityValidationException
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.validator.ValidationResult
import domain.validator.warehouse.WarehouseIdValidator

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: WarehouseIdValidator
) {
    suspend operator fun invoke(id: String): Warehouse? {
        if (idValidator.validate(id) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot get warehouse: invalid warehouse ID.")
        }

        return warehouseRepository.getById(id)
    }
}
